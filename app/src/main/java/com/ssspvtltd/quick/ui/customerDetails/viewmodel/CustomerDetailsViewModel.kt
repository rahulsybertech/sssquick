package com.ssspvtltd.quick.ui.customerDetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.model.customer.NickName

import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.CustomerDetailsRequest
import com.ssspvtltd.quick.ui.order.goodsreturn.repository.GoodsReturnRepository
import com.ssspvtltd.quick.utils.showToast

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
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

    private val _addCustomerResult = MutableLiveData<Boolean>()
    val addCustomerResult: LiveData<Boolean> = _addCustomerResult

    fun addCustomerDetailReq(customerDetailsRequest: CustomerDetailsRequest) = viewModelScope.launch {

        showProgressBar()

        when (val response = repository.addCustomerDetails(customerDetailsRequest)) {

            is ResultWrapper.Failure -> {
                hideProgressBar()
                apiErrorData(response.error)
                _addCustomerResult.postValue(false)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()

                val data = response.value.body()

                msg = data?.ResponseMessage ?: ""
                showToast(msg)

                _addCustomerResult.postValue(true)
            }
        }
    }




  }

