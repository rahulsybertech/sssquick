package com.ssspvtltd.quick_app.ui.order.hold.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick_app.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_app.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick_app.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_app.model.HoldOrderRequest
import com.ssspvtltd.quick_app.model.order.hold.HoldOrderData
import com.ssspvtltd.quick_app.model.progress.ProgressConfig
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.ui.order.hold.repository.HoldOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HoldOrderViewModel @Inject constructor(
    private val gson: Gson,
    private val repository: HoldOrderRepository
) : RecyclerWidgetViewModel() {


    private var holdOrderList = listOf<HoldOrderData>()
    var searchValue = ""
    fun getHoldOrder() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        val req = HoldOrderRequest(null,null,null,null, null)
        when (val response = repository.holdOrderList(req)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                holdOrderList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }

    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        holdOrderList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.orderItemList
                else it.orderItemList?.filter {
                    it.orderNo?.contains(searchValue, true) == true ||
                            it.salePartyName?.contains(searchValue, true) == true ||
                            it.supplierName?.contains(searchValue, true) == true
                }
            if (orderItemList?.isNotEmpty() == true) {
                list.add(TitleSubtitleWrapper(id = it.orderDate!!, title = it.orderDate))
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