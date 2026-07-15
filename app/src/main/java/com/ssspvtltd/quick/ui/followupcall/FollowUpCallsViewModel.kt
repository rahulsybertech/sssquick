package com.ssspvtltd.quick.ui.followupcall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.model.customer.AccountNameResponse
import com.ssspvtltd.quick.model.customer.NickName
import com.ssspvtltd.quick.model.customerdetails.CustomerList
import com.ssspvtltd.quick.model.editCustomer.EditCustomerData
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.customerDetails.model.CreateData
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.CustomerDetailsRequest
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.DeleteAccountRequest
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.DeleteLeadRequest
import com.ssspvtltd.quick.ui.order.goodsreturn.repository.GoodsReturnRepository
import com.ssspvtltd.quick.ui.tour.model.UpdateLeadResponse
import com.ssspvtltd.quick.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowUpCallsViewModel @Inject constructor(
    private val repository: GoodsReturnRepository
) : RecyclerWidgetViewModel()
{
    private val _accountNameList = MutableLiveData<List<AccountName>>()
    val customerList: LiveData<List<AccountName>> = _accountNameList
    var msg = ""

    fun getCustomerList() = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.getAccountNameListt()) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()

                _accountNameList.postValue(list!!.AccountNameList)
            }
        }
    }



    private val _customerNickNameList = MutableLiveData<List<NickName>>()
    val customerNickNameList: LiveData<List<NickName>> = _customerNickNameList

    fun factchCustomerNickNameList() = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.getNickNameList()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()
                _customerNickNameList.postValue(list!!.NickNameList)
            }
        }
    }



    /*   fun loadCustomerAndNickNameData() = viewModelScope.launch {

           showProgressBar()

           try {
               // Run both APIs in parallel
               val customerDeferred = async { repository.getAccountNameListt() }
               val nickNameDeferred = async { repository.getNickNameList() }

               // Wait for both
               val customerResponse = customerDeferred.await()
               val nickNameResponse = nickNameDeferred.await()

               // Handle Customer List
               if (customerResponse is ResultWrapper.Success) {
                   val list = customerResponse.value.body()
                   _accountNameList.postValue(list?.AccountNameList ?: emptyList())
               } else if (customerResponse is ResultWrapper.Failure) {
                   apiErrorData(customerResponse.error)
               }

               // Handle NickName List
               if (nickNameResponse is ResultWrapper.Success) {
                   val list = nickNameResponse.value.body()
                   _customerNickNameList.postValue(list?.NickNameList ?: emptyList())
               } else if (nickNameResponse is ResultWrapper.Failure) {
                   apiErrorData(nickNameResponse.error)
               }

           } catch (e: Exception) {
            //   apiErrorData(e.message ?: "Something went wrong")
           } finally {
               hideProgressBar() // ✅ only once after both APIs complete
           }
       }*/






    private val _customerListByNickNameId = MutableLiveData<List<AccountName>>()
    val customerListByNickNameId: LiveData<List<AccountName>> = _customerListByNickNameId

    fun factchCustomerListByNickNameId(id:String) = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.customerNameByNickName(id)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()

                _customerListByNickNameId.postValue(list!!.AccountNameList)
            }
        }
    }

    private val _addCustomerResult = MutableLiveData<AccountNameResponse>()
    val addCustomerResult: LiveData<AccountNameResponse> = _addCustomerResult
    var isSuccess=false


    fun addCustomerDetailReq(customerDetailsRequest: CustomerDetailsRequest) = viewModelScope.launch {

        showProgressBar()

        when (val response = repository.addCustomerDetails(customerDetailsRequest)) {

            is ResultWrapper.Failure -> {
                isSuccess=false
                hideProgressBar()
                apiErrorData(response.error)
                //    _addCustomerResult.postValue()
            }

            is ResultWrapper.Success -> {
                hideProgressBar()

                val data = response.value.body()

                if (data != null) {
                    val msg = data.ResponseMessage ?: ""
                    showToast(msg)

                    _addCustomerResult.postValue(data)
                } else {
                    showToast("Something went wrong, empty response")
                }
            }
        }
    }


    private val _allAccountList = MutableLiveData<List<CustomerList>>()
    val allAccountList: LiveData<List<CustomerList>> = _allAccountList
    private var customerList1 = listOf<CustomerList>()

    fun fatchAllAccountList() = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.allAccountReq()) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()!!.Data
                customerList1=list
                _allAccountList.postValue(list)
            }
        }
    }



    private val _deleteAccountForID = MutableLiveData<Boolean>()
    val deleteAccountForID: LiveData<Boolean> = _deleteAccountForID


    fun deleteAccountForIDParam(id: String,nickNameID:String,customerID:String) = viewModelScope.launch {

        showProgressBar()


        when (val response = repository.deleteAccountForIDReq(DeleteAccountRequest(id))) {

            is ResultWrapper.Failure -> {
                hideProgressBar()
                apiErrorData(response.error)
                _deleteAccountForID.postValue(false)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()

                val data = response.value.body()
                msg = data?.ResponseMessage ?: ""
                showToast(msg)

                _deleteAccountForID.postValue(true)
            }
        }
    }
    private val _accountDetailsForID = MutableLiveData<List<EditCustomerData>>()
    val accountDetailsForID: LiveData<List<EditCustomerData>> = _accountDetailsForID

    fun fatchAccountDetailsForID(jsObject: JsonObject) = viewModelScope.launch {
        showProgressBar()
        when (val response = repository.accountDetailsForIdReq(jsObject)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()
                val list = response.value.body()!!.Data
                //   customerList1=list
                _accountDetailsForID.postValue(list)
            }
        }
    }



    private val _customerListbyCustomerCode = MutableStateFlow<List<CreateData>>(emptyList())
    val customerListbyCustomerCode: StateFlow<List<CreateData>> = _customerListbyCustomerCode



    private val searchQuery = MutableStateFlow("")

    fun observeSearch() {

        viewModelScope.launch {

            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->

                    fatchCustomerListByCoustomerCode(query)

                }
        }
    }

    fun searchCustomer(query: String) {

        viewModelScope.launch {

            if (query.length >= 2) {

                searchQuery.emit(query)

            } else if (query.isEmpty()) {

                searchQuery.emit("")
            }
        }
    }
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun getfollowpCallList1(marketerId: String?, customerType: String) = viewModelScope.launch {

        _loading.value = true

        when (val response = repository.getfollowpCallReq(marketerId, customerType)) {

            is ResultWrapper.Success -> {

                _loading.value = false

                _getAllLeadtList.value =
                    response.value.body()?.data ?: emptyList()
            }

            is ResultWrapper.Failure -> {

                _loading.value = false
            }
        }
    }
    private suspend fun fatchCustomerListByCoustomerCode(search: String) {

        when (val response = repository.customerListByCustomerCodeReq(search)) {

            is ResultWrapper.Success -> {

                _customerListbyCustomerCode.value =
                    response.value.body()?.Data ?: emptyList()
            }

            is ResultWrapper.Failure -> {

            }
        }
    }
    private val _getAllLeadtList = MutableLiveData<List<LeadData>>()
    val getAllLeadByUserIDtList: LiveData<List<LeadData>> = _getAllLeadtList

    private val _getAllLeadtList1 = MutableLiveData<List<LeadData>>()
    val getAllLeadByUserIDtList1: LiveData<List<LeadData>> = _getAllLeadtList1

    fun clearList() {
        _getAllLeadtList.value = emptyList()
        _getAllLeadtList1.value = emptyList()
    }

    fun getfollowpCallList(marketerId: String?, customerType: String) = viewModelScope.launch {

        _getAllLeadtList.value = emptyList()   // Clear LiveData first

        showProgressBar()

        when (val response = repository.getfollowpCallReq(marketerId, customerType)) {

            is ResultWrapper.Success -> {

                hideProgressBar()
                //filterList = response.value.body()?.data ?: emptyList()
                _getAllLeadtList.value =
                    response.value.body()?.data ?: emptyList()
            }

            is ResultWrapper.Failure -> {
                hideProgressBar()
            }
        }
    }



    fun getfollowpCallList2(marketerId: String?, customerType: String) = viewModelScope.launch {

        _getAllLeadtList1.postValue(emptyList()) // Clear old data immediately

        showProgressBar()

        when (val response = repository.getfollowpCallReq(marketerId, customerType)) {

            is ResultWrapper.Failure -> {
                hideProgressBar()
                apiErrorData(response.error)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()

                val list = response.value.body()?.data ?: emptyList()

              //  filterList = list
                _getAllLeadtList1.postValue(list.toList())
            }
        }
    }

    var searchValue = ""
    private var filterList = listOf<LeadData>()
    fun filterList(query: String) {
        if (query.isBlank()) {
            _getAllLeadtList.value = filterList
            return
        }

        val filteredList = filterList.filter { customer ->
            customer.accountName?.contains(query, ignoreCase = true) == true||
            customer.subParty?.contains(query, ignoreCase = true) == true ||
                    customer.mobileNo?.contains(query, ignoreCase = true) == true ||
                    customer.leadNo?.contains(query, ignoreCase = true) == true ||
                    customer.lastFollowupDate?.contains(query, ignoreCase = true) == true ||
                    customer.remark?.contains(query, ignoreCase = true) == true ||
                    customer.date?.contains(query, ignoreCase = true) == true ||
                    customer.clubType?.contains(query, ignoreCase = true) == true ||
                    customer.partyType?.contains(query, ignoreCase = true) == true
        }

        _getAllLeadtList.value = filteredList
    }
    private val _msg = MutableLiveData<String>()
    val updateLeadResponse: LiveData<String> = _msg
    fun updateLead(marketerId: String?, customerType: String) = viewModelScope.launch {
        showProgressBar()

        when (val response = repository.getupdateLeadReq(marketerId,customerType,"","")) {

            is ResultWrapper.Failure -> apiErrorData(response.error)

            is ResultWrapper.Success -> {
                hideProgressBar()

                _msg.value = response.value.body()?.applicationMessage ?: ""
            }
        }
    }


    fun deleteLeadForIDParam(id: String) = viewModelScope.launch {

        showProgressBar()


        when (val response = repository.deleteLeadForIDReq(DeleteLeadRequest(id))) {

            is ResultWrapper.Failure -> {
                hideProgressBar()
                apiErrorData(response.error)
                _deleteAccountForID.postValue(false)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()

                val data = response.value.body()
                msg = data?.ResponseMessage ?: ""
                showToast(msg)

                _deleteAccountForID.postValue(true)
            }
        }
    }
}