package com.ssspvtltd.quick_new.ui.checkincheckout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_new.constants.ButtonType
import com.ssspvtltd.quick_new.constants.CheckInType
import com.ssspvtltd.quick_new.model.checkincheckout.CheckInRequest
import com.ssspvtltd.quick_new.model.checkincheckout.CustomerData
import com.ssspvtltd.quick_new.model.progress.ProgressConfig
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.ui.checkincheckout.repository.CheckInCheckOutRepository
import com.ssspvtltd.quick_new.utils.extension.isNotNullOrBlank
import com.ssspvtltd.quick_new.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CheckInCheckOutViewModel @Inject constructor(
    private val gson: Gson,
    private val repository: CheckInCheckOutRepository,
) : RecyclerWidgetViewModel() {

    private val _selectedTab = MutableLiveData<CheckInType>()
    val selectedTab: LiveData<CheckInType> get() = _selectedTab

    private val _buttonType = MutableLiveData<ButtonType>()
    val buttonType: LiveData<ButtonType> get() = _buttonType

    private val _ckeckoutSuccessData = MutableLiveData<Boolean>()
    val ckeckoutSuccessData: LiveData<Boolean> get() = _ckeckoutSuccessData

    private val _addUpdateSuccessData = MutableLiveData<Boolean>()
    val addUpdateSuccessData: LiveData<Boolean> get() = _addUpdateSuccessData

    private val _activeTab = MutableLiveData<String>()
    val activeTab: LiveData<String> get() = _activeTab

    private val _counts = MutableLiveData<Triple<Int, Int, Int>>()
    val counts: LiveData<Triple<Int, Int, Int>> get() = _counts

    private val _isListModified = MutableLiveData<Boolean>(false)
    val isListModified get():LiveData<Boolean> = _isListModified

    private val _isUpdateButtonVisible = MutableLiveData<Boolean>(false)
    val isUpdateButtonVisible get():LiveData<Boolean> = _isUpdateButtonVisible

    private var _setRemark = MutableLiveData<String>()
    val getRemark get():LiveData<String> = _setRemark

    var searchValue: String? = ""
    private var mainList = listOf<CustomerData>()
    private var unmodifiedMainList = listOf<CustomerData>()
    var apiRemark: String? = ""
    var remark: String? = ""
    // var selectedItem = listOf<CustomerData>()

    fun getCustomer(isProgressVisible: Boolean) = viewModelScope.launch { 
        if (isProgressVisible)
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        else {
            hideProgressBar()
        }
        Log.e("respo", repository.customerList().toString())
        when (val response = repository.customerList()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    val tabType = when {
                        it.any { data -> data.checkInType == CheckInType.ORDER_IN_MARKET.value } -> CheckInType.ORDER_IN_MARKET
                        it.any { data -> data.checkInType == CheckInType.WITH_MARKETER.value } -> CheckInType.WITH_MARKETER
                        it.any { data -> data.checkInType == CheckInType.ORDER_IN_OFFICE.value } -> CheckInType.ORDER_IN_OFFICE
                        else -> if (response.value.remark != null) {
                            CheckInType.OTHER
                        } else {
                            CheckInType.ORDER_IN_OFFICE
                        }
                    }
                    mainList = it.filter { it.accountCode.isNotNullOrBlank() }
                    unmodifiedMainList = ArrayList(mainList)
                    // _isListModified.postValue(false)
                    validateUpdateButton()
                    // selectedItem = it.filter { it.chkStatus == true }
                    prefHelper.setCheckinStatus(response.value.checkinStatus ?: false)
                    val btnType =
                        if (response.value.checkinStatus == true) ButtonType.CHECK_OUT else ButtonType.CHECK_IN
                    // val btnType = if (selectedItem.isEmpty()) ButtonType.CHECK_IN else ButtonType.CHECK_OUT

                    clearWidgetList()
                    addItemToWidgetList(mainList)
                    withContext(Dispatchers.Main) {
                        _selectedTab.postValue(tabType)
                        _buttonType.postValue(btnType)
                        _setRemark.postValue(response.value.remark.toString())
                        remark = response.value.remark.toString()
                        apiRemark = remark
                        listDataChanged()
                        hideProgressBar()
                    }
                }
            }
        }
    }

    fun setTabSelection(tabType: CheckInType) = viewModelScope.launch(Dispatchers.Default) {
        when (tabType) {
            CheckInType.ORDER_IN_OFFICE -> {
                _activeTab.postValue(tabType.value)
                if (mainList.any { it.checkInType == CheckInType.ORDER_IN_MARKET.value || it.checkInType == CheckInType.WITH_MARKETER.value || remark.isNotNullOrBlank() }) {
                    showMsgAlert(R.string.order_in_market_msg)
                    return@launch
                }
            }

            else -> {
                _activeTab.postValue(tabType.value)
                if (mainList.any { it.checkInType == CheckInType.ORDER_IN_OFFICE.value }) {
                    showMsgAlert(R.string.order_in_office_msg)
                    return@launch
                }
            }
        }
        withContext(Dispatchers.Main) { _selectedTab.value = tabType }
    }

    fun searchData(searchParam: String?) = viewModelScope.launch(Dispatchers.Default) {
        clearWidgetList()
        if (searchParam.isNullOrBlank()) {
            addItemToWidgetList(mainList)
        } else {
            val filterList = mainList.filter { it.accountCode?.contains(searchParam, true) == true }
            addItemToWidgetList(filterList)
        }
        withContext(Dispatchers.Main) { listDataChanged() }
    }

    fun checkUncheckItem(data: CustomerData) = viewModelScope.launch(Dispatchers.Default) {
        if (data.tlock == true) {
            withContext(Dispatchers.Main) {
                showMsgAlert(
                    "Sorry!",
                    message = data.alertMessage,
                    SweetAlertDialog.WARNING_TYPE,
                    btnBgColor = R.color.orange_200
                )
            }
            return@launch
        } else if (data.checkInType.isNotNullOrBlank() && data.checkInType != selectedTab.value?.value) {
            withContext(Dispatchers.Main) {
                showMsgAlert(
                    "Sorry!",
                    "Already selected in ${data.checkInType}",
                    SweetAlertDialog.WARNING_TYPE,
                    btnBgColor = R.color.yellow_light
                )
            }
            return@launch
        }
        val newData = data.copy().apply {
            val checkValue = !(chkStatus ?: false)
            checkInType = if (checkValue) selectedTab.value?.value else ""
            chkStatus = checkValue
        }
        mainList = mainList.map { if (it == data) newData else it }
        // _isListModified.postValue(mainList != unmodifiedMainList)
        validateUpdateButton()
        val filterList =
            mainList.filter { it.accountCode?.contains(searchValue.toString(), true) == true }
        changeAllWidgetList(filterList)
        withContext(Dispatchers.Main) { listDataChanged() }
    }

    fun addUpdateCustomer(callBack: (() -> Unit)? = null) = viewModelScope.launch(Dispatchers.Default) {
        val selectedCustomers = mainList.filter { it.chkStatus == true }

        // if (selectedCustomers.isEmpty() && activeTab.value.equals(CheckInType.ORDER_IN_OFFICE.value) ) {

        if (selectedCustomers.isEmpty() && remark.isNullOrBlank()) {
            showToast(remark)
            showMsgAlert(
                message = "No item selected",
                type = SweetAlertDialog.WARNING_TYPE,
                btnBgColor = R.color.yellow_light
            )
            return@launch
        }
        val typeToken = object : TypeToken<List<CustomerData>>() {}.type
        val customerString = gson.toJson(selectedCustomers, typeToken)
        Log.e("addUpdateCustomer", customerString)
        val checkInRequest = CheckInRequest(
            "Office No - 157, Corenthum Noida - 62",
            selectedCustomers,
            "77.055544",
            "78.545454",
            remark.toString()
        )
        Log.e("checkInRequest", gson.toJson(checkInRequest))

        withContext(Dispatchers.Main) {
            showProgressBar(ProgressConfig("Sending Data\nPlease wait..."))
        }
        val response = repository.addUpdateCustomerList(checkInRequest)
        when (response) {
            is ResultWrapper.Failure -> withContext(Dispatchers.Main) {
                apiErrorData(response.error)
                Log.e("response.error", response.toString())
                apiRemark = remark
                _addUpdateSuccessData.postValue(false)
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Main) {
                _addUpdateSuccessData.postValue(true)
                prefHelper.setCheckinStatus(true)
                hideProgressBar()
                showMsgAlert(
                    "Success!",
                    response.value.message,
                    SweetAlertDialog.SUCCESS_TYPE,
                    btnBgColor = R.color.green_light,
                    callback = callBack
                )
            }
        }
    }

    fun checkOutCustomer() = viewModelScope.launch(Dispatchers.Default) {
        val selectedCustomers = mainList.filter { it.chkStatus == true }
        if (selectedCustomers.isEmpty() && remark.isNullOrBlank()) {
            showMsgAlert(
                "Oops!",
                "No item Selected",
                SweetAlertDialog.WARNING_TYPE,
                btnBgColor = R.color.yellow_light
            )
            return@launch
        }

        showProgressBar(ProgressConfig("Checking Out\nPlease wait..."))
        when (val response = repository.checkOutCustomer()) {
            is ResultWrapper.Failure -> withContext(Dispatchers.Main) {
                apiErrorData(response.error)
                _ckeckoutSuccessData.postValue(false)
            }

            is ResultWrapper.Success -> {
                prefHelper.setCheckinStatus(false)
                _ckeckoutSuccessData.postValue(true)
                mainList = mainList.map { it.copy(checkInType = null, chkStatus = false) }
                unmodifiedMainList = mainList
                // _isListModified.postValue(false)
                validateUpdateButton()
                changeAllWidgetList(mainList)
                withContext(Dispatchers.Main) {
                    listDataChanged()
                    hideProgressBar()

                    showMsgAlert(
                        "Success!",
                        response.value.message,
                        SweetAlertDialog.SUCCESS_TYPE,
                        btnBgColor = R.color.green_light
                    )
                }
            }
        }
    }

    fun updateCount() = viewModelScope.launch(Dispatchers.Default) {
        var count1 = 0
        var count2 = 0
        var count3 = 0
        mainList.forEach {
            if (it.chkStatus == true) {
                when (it.checkInType) {
                    CheckInType.ORDER_IN_OFFICE.value -> count1++
                    CheckInType.ORDER_IN_MARKET.value -> count2++
                    CheckInType.WITH_MARKETER.value -> count3++
                    else -> {}
                }
            }
        }
        _counts.postValue(Triple(count1, count2, count3))
    }

    fun validateUpdateButton() = viewModelScope.launch(Dispatchers.Default) {
        _isUpdateButtonVisible.postValue(
            buttonType.value == ButtonType.CHECK_OUT &&
                    (apiRemark != remark || mainList != unmodifiedMainList)
        )
    }
}