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
    private val _totalAmountLiveData = MutableLiveData<Int>()
    val totalAmountLiveData: LiveData<Int> get() = _totalAmountLiveData
    private val _isAmountLoading = MutableLiveData<Boolean>()
    val isAmountLoading: LiveData<Boolean> get() = _isAmountLoading

    var searchValue = ""
    fun getStockInOffice() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.stockInOffice(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,"1","50"
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
                _isAmountLoading.postValue(true)
                prepareFilteredList()
            }
        }
    }

    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        var totalAmount = 0   // 👈 total amount

        stockInOfficeList.forEach { stock ->
            val orderItemList =
                if (searchValue.isBlank()) stock.orderItemList
                else stock.orderItemList.filter {
                    it.supplierName.contains(searchValue, true) ||
                            it.salePartyName.contains(searchValue, true) ||
                            (it.supplierMob?.contains(searchValue, true) == true)
                }

            if (orderItemList.isNotEmpty()) {
                list.add(TitleSubtitleWrapper(id = stock.orderDate, title = stock.orderDate))
                list.addAll(orderItemList)

                count += orderItemList.size

                // 👇 ADD AMOUNT
                totalAmount += orderItemList.sumOf { it.amount }
            }
        }

        withContext(Dispatchers.Main) {
            clearWidgetList()
            addItemToWidgetList(list)
            listDataChanged()

            // 🔥 publish total amount
            _totalAmountLiveData.value = totalAmount
            _isAmountLoading.value = false
        }
    }

}