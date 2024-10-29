package com.ssspvtltd.quick_app.ui.order.pendinglr.viewmodel

import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick_app.base.recycler.data.BaseWidget
import com.ssspvtltd.quick_app.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick_app.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_app.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick_app.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick_app.model.progress.ProgressConfig
import com.ssspvtltd.quick_app.networking.ResultWrapper
import com.ssspvtltd.quick_app.ui.order.pendinglr.repository.PendingLrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PendingLrViewmodel @Inject constructor(
    private val repository: PendingLrRepository
) : RecyclerWidgetViewModel() {

    private var pendingLrList = listOf<PendingLrData>()
    var searchValue = ""
    fun getPendingLr() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.pendingLrList(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null
            )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                pendingLrList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }

    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        pendingLrList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.saleDetailList
                else it.saleDetailList?.filter {
                    it.saleBookID?.contains(searchValue, true) == true ||
                            it.salePartyName?.contains(searchValue, true) == true ||
                            it.supplierName?.contains(searchValue, true) == true
                }
            if (orderItemList?.isNotEmpty() == true) {
                list.add(TitleSubtitleWrapper(id = it.billDate, title = it.billDate))
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