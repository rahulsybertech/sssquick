package com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.GoodsReturnImageUploadRequest
import com.ssspvtltd.quick.model.grNew.GoodsReturnDataNew
import com.ssspvtltd.quick.model.grNew.GoodsReturnItem
import com.ssspvtltd.quick.model.grNew.GoodsReturnResponse
import com.ssspvtltd.quick.model.mailbox.MailData
import com.ssspvtltd.quick.model.mailbox.MailDetail
import com.ssspvtltd.quick.model.mailbox.MailResponse
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
import com.ssspvtltd.quick.ui.order.goodsreturn.repository.GoodsReturnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GoodsReturnViewModel @Inject constructor(
    private val repository: GoodsReturnRepository
) : RecyclerWidgetViewModel() {
    private var goodsReturnList = listOf<GoodsReturnData>()
    private var goodsReturnDetailList = listOf<GoodsReturnDataNew>()

    private var originalList = listOf<GoodsReturnDataNew>()
    private var mailResponse = listOf<MailResponse>()
    private var mailList = listOf<MailData>()

    private var goodsReturnItem = listOf<GoodsReturnItem>()
    var searchValue = ""
    fun getGoodsReturn() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getGoodsReturn(
            GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null,"1","50"
        )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                goodsReturnList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }

    fun getMailBox() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getMailBox(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,"1","50"
            )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                mailList = response.value.data.orEmpty()
              //  mailList.addAll(mailResponse.get(0).data)


                prepareFilteredMailBoxList()
            }
        }
    }

    fun getGoodsReturnSecondary(id:String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getGoodsReturnSecondary(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,"1","50"
            )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                goodsReturnList = response.value.data.orEmpty()
                prepareFilteredSecondaryList(id)
            }
        }
    }

    private val _goodsReturnListnew = MutableLiveData<List<GoodsReturnDataNew>>()
    val goodsReturnListnew: LiveData<List<GoodsReturnDataNew>> = _goodsReturnListnew






    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        goodsReturnList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.goodsReturnPrimaryData
                else it.goodsReturnPrimaryData?.filter {
                    it.saleBillNo?.contains(searchValue, true) == true ||
                            it.salePartyName?.contains(searchValue, true) == true ||
                            it.supplierName?.contains(searchValue, true) == true
                }
            if (orderItemList?.isNotEmpty() == true) {
                list.add(TitleSubtitleWrapper(id = it.billDate!!, title = it.billDate))
                list.addAll(orderItemList)
                count += orderItemList.size
            }
        }
        clearWidgetList()
        addItemToWidgetList(list)
        withContext(Dispatchers.Main) {
            listDataChanged()
            hideProgressBar()
        }
    }

    fun prepareFilteredSecondaryList(id: String) = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0

        goodsReturnList.forEach { data ->
            if (data.id == id) {  // ✅ Match only specific ID
                val orderItemList = if (searchValue.isBlank()) {
                    data.goodsReturnDetailList
                } else {
                    data.goodsReturnDetailList?.filter {
                        it.saleBillNo?.contains(searchValue, true) == true ||
                                it.salePartyName?.contains(searchValue, true) == true ||
                                it.supplierName?.contains(searchValue, true) == true
                    }
                }

                if (!orderItemList.isNullOrEmpty()) {
                    list.add(TitleSubtitleWrapper(id = data.billDate!!, title = data.billDate))
                    list.addAll(orderItemList)
                    count += orderItemList.size
                }
            }
        }

        clearWidgetList()
        addItemToWidgetList(list)

        withContext(Dispatchers.Main) {
            listDataChanged()
            hideProgressBar()
        }
    }


    fun prepareFilteredListGr() = viewModelScope.launch(Dispatchers.Default) {
        val filteredList = originalList.filter { item ->
            searchValue.isBlank() || item.saleBillNo?.contains(searchValue, ignoreCase = true) == true
                    || item.salePartyName?.contains(searchValue, ignoreCase = true) == true
                    || item.billDate?.contains(searchValue, ignoreCase = true) == true
                    || item.supplierName?.contains(searchValue, ignoreCase = true) == true
                    || item.supplierName?.contains(searchValue, ignoreCase = true) == true
        }
        _goodsReturnListnew.postValue(filteredList)
    }

    fun prepareFilteredMailBoxList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        mailList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.mailDetailList
                else it.mailDetailList?.filter {
                    it.orderNo?.contains(searchValue, true) == true ||
                            it.saleParty?.contains(searchValue, true) == true ||
                            it.purchaseParty?.contains(searchValue, true) == true
                }
            if (orderItemList?.isNotEmpty() == true) {
                list.add(TitleSubtitleWrapper(id = it.billDate!!, title = it.billDate))
                list.addAll(orderItemList)
                count += orderItemList.size
            }
        }
        clearWidgetList()
        addItemToWidgetList(list)
        withContext(Dispatchers.Main) {
            listDataChanged()
            hideProgressBar()
        }
    }






}