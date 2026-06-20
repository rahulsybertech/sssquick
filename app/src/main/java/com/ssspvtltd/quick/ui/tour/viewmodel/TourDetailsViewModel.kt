package com.ssspvtltd.quick.ui.tour.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.tour.model.CategoryItem
import com.ssspvtltd.quick.ui.tour.model.CommonResponse
import com.ssspvtltd.quick.ui.tour.model.Data
import com.ssspvtltd.quick.ui.tour.model.GradeItem
import com.ssspvtltd.quick.ui.tour.model.LeadDetails
import com.ssspvtltd.quick.ui.tour.model.LeadRequest
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryItem
import com.ssspvtltd.quick.ui.tour.model.StateItem
import com.ssspvtltd.quick.ui.tour.model.StationItem
import com.ssspvtltd.quick.ui.tour.repository.TourDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourDetailsViewModel @Inject constructor(
    private val repository: TourDetailsRepository
) : RecyclerWidgetViewModel() {
    private val _frimList = MutableLiveData<List<AccountName>>()
    val frimList: LiveData<List<AccountName>> = _frimList
    var msg = ""

    fun getCustomerList() = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.getAccountNameListt()) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()

                _frimList.postValue(list!!.AccountNameList)
            }
        }
    }

    private val _accountDetailsByID =
        MutableLiveData<Data?>()

    val accountDetailsByID: LiveData<Data?> =
        _accountDetailsByID

    fun getAccountDetailsByID(accountDetailsByID: String?) = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.getAccountDetailsByIDReq(accountDetailsByID)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()!!.data
                //  customerList1=list
                _accountDetailsByID.value =
                    list
            }
        }
    }
    private val _submitLeadResponse = MutableLiveData<CommonResponse>()

    val submitLeadResponse: LiveData<CommonResponse>
        get() = _submitLeadResponse
    fun submitLead(leadRequest: LeadRequest) = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.submitLeadd(leadRequest)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val result = response.value.body()

                result?.let {

                    _submitLeadResponse.postValue(it)
                    // _frimList.postValue(list!!.AccountNameList)
                }
            }}
    }

    private val _gradeList = MutableLiveData<List<GradeItem>>()
    val gradeList: LiveData<List<GradeItem>> = _gradeList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


 /*   private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    fun fetchLocation() {
        viewModelScope.launch {
            repository.getLocationFlow()
                .catch { e ->
                    Log.e("LOCATION", "Error: ${e.message}")
                }
                .collect {
                    _location.value = it
                }
        }
    }*/
    fun getGradeList() {

        viewModelScope.launch {

            _loading.value = true
            showProgressBar()
            when (val response = repository.getgetGradeList()) {

                is ResultWrapper.Failure -> apiErrorData(response.error)

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    val list = response.value.body()

                    _gradeList.postValue(list!!.data)
                }
            }
        }
    }

    private val _stationList = MutableLiveData<List<StationItem>>()
    val stationList: LiveData<List<StationItem>> = _stationList

    fun getStationList(selectedStateId: String?) {

        viewModelScope.launch {

            _loading.value = true
            showProgressBar()
            when (val response = repository.getgetStationList(selectedStateId)) {

                is ResultWrapper.Failure -> apiErrorData(response.error)

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    val list = response.value.body()

                    _stationList.postValue(list!!.data)
                }
            }
        }
    }

    private val _stateList = MutableLiveData<List<StateItem>>()
    val stateList: LiveData<List<StateItem>> = _stateList

    fun getStateList() {
        viewModelScope.launch {

            _loading.value = true
            showProgressBar()
            when (val response = repository.getgetStateList()) {

                is ResultWrapper.Failure -> apiErrorData(response.error)

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    val list = response.value.body()

                    _stateList.postValue(list!!.data)
                }
            }
        }
    }

    private val _categoryList = MutableLiveData<List<CategoryItem>>()
    val categoryList: LiveData<List<CategoryItem>> = _categoryList

    fun getCategoryList() {
        viewModelScope.launch {

            _loading.value = true
            showProgressBar()
            when (val response = repository.getgetCategoryList()) {

                is ResultWrapper.Failure -> apiErrorData(response.error)

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    val list = response.value.body()

                    _categoryList.postValue(list!!.data)
                }
            }
        }
    }


    private val _shopcategoryList = MutableLiveData<List<ShopCategoryItem>>()
    val shopcategoryList: LiveData<List<ShopCategoryItem>> = _shopcategoryList

    fun getShopCategoryList() {
        viewModelScope.launch {

            _loading.value = true
            showProgressBar()
            when (val response = repository.getgetShopCategoryList()) {

                is ResultWrapper.Failure -> apiErrorData(response.error)

                is ResultWrapper.Success -> {
                    hideProgressBar()
                    val list = response.value.body()

                    _shopcategoryList.postValue(list!!.data)
                }
            }

        }
    }

    private val _getleadDetailByLedId =
        MutableLiveData<LeadDetails?>()

    val getleadDetailByLedId: LiveData<LeadDetails?> =
        _getleadDetailByLedId

    fun getLeadDetailByLeadID(leadID: String) = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.getLeadByLeadIDReq(leadID)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()!!.data
                //  customerList1=list
                _getleadDetailByLedId.value =
                    list
            }
        }
    }


}