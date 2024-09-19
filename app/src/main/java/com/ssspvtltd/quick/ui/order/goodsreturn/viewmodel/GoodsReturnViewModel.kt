package com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
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
    var searchValue = ""
    fun getGoodsReturn() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getGoodsReturn()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                goodsReturnList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }

    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        goodsReturnList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.goodsReturnDetailList
                else it.goodsReturnDetailList?.filter {
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
}