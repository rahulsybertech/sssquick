package com.ssspvtltd.quick.ui.order.stockinoffice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.order.stockinoffice.StockInOfficeData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.stockinoffice.repository.StockInOfficeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class StockInOfficeViewModel @Inject constructor(private val repository: StockInOfficeRepository) :
    RecyclerWidgetViewModel() {
    private var stockInOfficeList = listOf<StockInOfficeData>()

    val getMessage = MutableLiveData<String?>()

    private val _responseCodeOfSIO = MutableLiveData<String>()
    val responseCodeOfSIO: LiveData<String> get() = _responseCodeOfSIO

    val responseMessageOfSIO = MutableLiveData<String>()

    var searchValue = ""
    fun getStockInOffice() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.stockInOffice(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null
        )
        )) {
            is ResultWrapper.Failure -> {

                hideProgressBar()
                apiErrorData(response.error)
            }
            is ResultWrapper.Success -> {
                stockInOfficeList = response.value.data.orEmpty()
                _responseCodeOfSIO.postValue(response.value.responseCode.orEmpty())
                getMessage.value = response.value.message.orEmpty()
                hideProgressBar()
                prepareFilteredList()
            }
        }
    }

    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        stockInOfficeList.forEach {
            val orderItemList = if (searchValue.isBlank()) it.orderItemList
            else it.orderItemList?.filter {
                it.supplierName?.contains(searchValue, true) == true || it.salePartyName?.contains(
                    searchValue,
                    true
                ) == true || it.supplierMob?.contains(searchValue, true) == true
            }
            if (orderItemList?.isNotEmpty() == true) {
                list.add(TitleSubtitleWrapper(id = it.orderDate, title = it.orderDate))
                list.addAll(orderItemList)
                count += orderItemList.size
            }
        }
        clearWidgetList()
        addItemToWidgetList(list)
        withContext(Dispatchers.Main) {
            listDataChanged()
            //hideProgressBar()
        }
    }
}