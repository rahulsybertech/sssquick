package com.ssspvtltd.quick.ui.order.hold.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.HoldOrderRequest
import com.ssspvtltd.quick.model.order.hold.HoldOrderData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.hold.repository.HoldOrderRepository
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

    var totalAmount = 0.0
        private set
    private var isHoldOrderLoaded = false

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    fun getHoldOrder1() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        val req = HoldOrderRequest(null,null,null,null, null)
        when (val response = repository.holdOrderList(req)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                println("GETTING_RESPONSE_HOLD  ${Gson().toJson(response.value.data?.get(0))}")
                holdOrderList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }
    fun getHoldOrder(forceRefresh: Boolean = false) =
        viewModelScope.launch {

            if (isHoldOrderLoaded && !forceRefresh) {
                prepareFilteredList()
                return@launch
            }

            _isLoading.value = true

            try {

                val req = HoldOrderRequest(
                    null,
                    null,
                    null,
                    null,
                    null
                )

                when (val response =
                    repository.holdOrderList(req)) {

                    is ResultWrapper.Failure -> {
                        apiErrorData(response.error)
                    }

                    is ResultWrapper.Success -> {

                        holdOrderList =
                            response.value.data.orEmpty()

                        isHoldOrderLoaded = true

                        prepareFilteredList()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()

            } finally {

                _isLoading.value = false
            }
        }
    var totalOrderCount: String = ""
        private set
    suspend fun prepareFilteredList() {

        val result = withContext(Dispatchers.Default) {

            val list = mutableListOf<BaseWidget>()

            var total = 0.0
            var orderCount = 0

            holdOrderList.forEach { order ->

                val orderItemList =
                    if (searchValue.isBlank()) {
                        order.orderItemList
                    } else {
                        order.orderItemList?.filter { item ->

                            item.orderNo?.contains(
                                searchValue,
                                ignoreCase = true
                            ) == true ||

                                    item.salePartyName?.contains(
                                        searchValue,
                                        ignoreCase = true
                                    ) == true ||

                                    item.supplierName?.contains(
                                        searchValue,
                                        ignoreCase = true
                                    ) == true
                        }
                    }

                if (!orderItemList.isNullOrEmpty()) {

                    // Actual order count
                    orderCount += orderItemList.size

                    // Total amount
                    orderItemList.forEach { item ->
                        total += item.amount ?: 0.0
                    }

                    // Date header
                    list.add(
                        TitleSubtitleWrapper(
                            id = order.orderDate.orEmpty(),
                            title = order.orderDate.orEmpty()
                        )
                    )

                    // Orders
                    list.addAll(orderItemList)
                }
            }

            Triple(list, total, orderCount)
        }

        totalAmount = result.second
        totalOrderCount = "${result.third} records"

        clearWidgetList()
        addItemToWidgetList(result.first)

        listDataChanged()
    }
}