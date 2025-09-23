package com.ssspvtltd.quick.ui.order.pending.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.constants.ButtonType
import com.ssspvtltd.quick.constants.CheckInType
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.pending.repository.PendingOrderRepository
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import com.ssspvtltd.quick.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PendingOrderViewModel @Inject constructor(
    private val gson: Gson,
    private val repository: PendingOrderRepository,

    ) : RecyclerWidgetViewModel() {

    private val _mCountLiveData = MutableLiveData<ApiResponse<*>?>()
    val mCountLiveData: LiveData<ApiResponse<*>?> get() = _mCountLiveData

    private val _responseCodeOfPendingList = MutableLiveData<String>()
    val responseCodeOfPendingList: LiveData<String> get() = _responseCodeOfPendingList

    val responseMessageOfPendingList = MutableLiveData<String>()

    private var pendingOrderList = listOf<PendingOrderData>()
    var searchValue = ""


    fun getCount() = viewModelScope.launch {

        when (val response = repository.count(    GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null,"1","10"
        )
        )) {

            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                prefHelper.setCount(response.value.data.toString())
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _mCountLiveData.postValue(response.value)
                }

            }
        }


    }


    fun getCountCustomer(id:String) = viewModelScope.launch {

        when (val response = repository.countCustomer(id)) {

            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                prefHelper.setCount(response.value.data.toString())
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _mCountLiveData.postValue(response.value)
                }

            }
        }


    }





    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0

        for (order in pendingOrderList) {
            val filteredItems = if (searchValue.isBlank()) {
                order.orderItemList
            } else {
                order.orderItemList?.filter {
                    it.orderNo?.contains(searchValue, true) == true ||
                            it.salePartyName?.contains(searchValue, true) == true ||
                            it.supplierName?.contains(searchValue, true) == true
                }
            }

            if (!filteredItems.isNullOrEmpty()) {
                list.add(TitleSubtitleWrapper(id = order.orderDate, title = order.orderDate))
                list.addAll(filteredItems)
                count += filteredItems.size
            }
        }

        withContext(Dispatchers.Main) {
            clearWidgetList()
            addItemToWidgetList(list)
            listDataChanged()
            hideProgressBar()
        }
    }


    private var totalCount: Int? = null
    private val itemsPerCall = 10
    private var hasFetchedPage1 = false
    private var mainList = listOf<CustomerData>()
    private val _customerDataResp = MutableLiveData<List<CustomerData>>()
    val customerDataResp: LiveData<List<CustomerData>> get() = _customerDataResp


    // Step 1: Fetch Page 1 Immediately
    fun fetchOrdersPage(
        page: Int,
        accumulatedList: MutableList<PendingOrderData>
    ) = viewModelScope.launch {
        val request = FilterRequest(
            buyerIDs = null,
            fromDate = null,
            supplierIDs = null,
            toDate = null,
            isSupplier = true,
            pageNumber = page.toString(),
            pageSize = itemsPerCall.toString()
        )

        when (val response = repository.pendingOrderList(request)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                _responseCodeOfPendingList.postValue(response.value.responseCode.orEmpty())
                responseMessageOfPendingList.value = response.value.message.orEmpty()

                val data = response.value.data.orEmpty()
                accumulatedList.addAll(data)

                pendingOrderList = accumulatedList
                prepareFilteredList()

                hasFetchedPage1 = true

                // Continue with remaining pages if count already known
                totalCount?.let {
                    fetchRemainingPages(2, it, accumulatedList)
                }
            }
        }
    }





    private val _countText = MutableLiveData<String>()
    val countText: LiveData<String> = _countText
    // Step 2: Fetch Count in Background
    fun fetchCountInBackground() = viewModelScope.launch {
        val response = repository.count(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,
                pageNumber = "1",
                pageSize = "10"
            )
        )

        when (response) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                val count: Int = when (val data = response.value.data) {
                    is Int -> data
                    is Double -> data.toInt()
                    is Float -> data.toInt()
                    is String -> data.toDoubleOrNull()?.toInt() ?: 10
                    else -> 10
                }

                totalCount = count
                prefHelper.setCount(count.toString())
                _countText.postValue("$count records")
                _mCountLiveData.postValue(response.value)
                if (hasFetchedPage1) {
                    fetchRemainingPages(2, count, pendingOrderList.toMutableList())
                }
            }
        }
    }

    // Step 3: Fetch Remaining Pages (after count is received)
    private suspend fun fetchRemainingPages(
        startPage: Int,
        totalCount: Int,
        accumulatedList: MutableList<PendingOrderData>
    ) {
        val fullPages = totalCount / itemsPerCall
        val remainder = totalCount % itemsPerCall
        val maxPage = if (remainder == 0) fullPages else fullPages + 1

        for (page in startPage..maxPage) {
            val isLastPage = page == maxPage
            val pageSize = if (isLastPage && remainder != 0) remainder else itemsPerCall

            val request = FilterRequest(
                buyerIDs = null,
                fromDate = null,
                supplierIDs = null,
                toDate = null,
                isSupplier = true,
                pageNumber = page.toString(),
                pageSize = pageSize.toString()
            )

            when (val response = repository.pendingOrderList(request)) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    return
                }
                is ResultWrapper.Success -> {
                    val data = response.value.data.orEmpty()
                    accumulatedList.addAll(data)

                    pendingOrderList = accumulatedList
                    prepareFilteredList()
                }
            }
        }
    }




    fun getCustomer(isProgressVisible: Boolean) = viewModelScope.launch {
      /*  if (isProgressVisible)
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        else {
            hideProgressBar()
        }*/
        // Log.e("respo", repository.customerList().toString())
        when (val response = repository.customerList()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    mainList = it.filter { it.accountCode.isNotNullOrBlank() }
                    _customerDataResp.postValue(mainList)
                  //  clearWidgetList()
                //    addItemToWidgetList(mainList)
                    withContext(Dispatchers.Main) {
                        listDataChanged()
                        hideProgressBar()
                    }
                }
            }
        }
    }



    fun fetchOrdersPageByCustomer(
        customerId: String,
        accumulatedList: MutableList<PendingOrderData>
    ) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        var page = 1
        val pageSize = 10
        var shouldContinue = true

        while (shouldContinue) {
            val response = repository.pendingOrderListByCustomer(customerId, page.toString(), pageSize.toString())

            when (response) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    hideProgressBar()
                    shouldContinue = false
                }

                is ResultWrapper.Success -> {
                    _responseCodeOfPendingList.postValue(response.value.responseCode.orEmpty())
                    responseMessageOfPendingList.value = response.value.message.orEmpty()

                    val data = response.value.data.orEmpty()
                    accumulatedList.addAll(data)
                    pendingOrderList = accumulatedList

                    withContext(Dispatchers.Default) {
                        prepareFilteredList()
                    }

                    // Stop if less than pageSize items are returned
                    if (data.size < pageSize) {
                        shouldContinue = false
                    } else {
                        page++ // Go to next page
                    }
                }
            }
        }

        hideProgressBar()
        hasFetchedPage1 = true
    }


    fun fetchOrdersPageByCustomerWithCount(customerId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        // Step 1: Get total count first
        val countResponse = repository.countCustomer(customerId)
        var totalCount = 0

        when (countResponse) {
            is ResultWrapper.Failure -> {
                apiErrorData(countResponse.error)
                hideProgressBar()
                return@launch
            }
            is ResultWrapper.Success -> {
                totalCount = when (val data = countResponse.value.data) {
                    is Int -> data
                    is Double -> data.toInt()
                    is Float -> data.toInt()
                    is String -> data.toDoubleOrNull()?.toInt() ?: 0
                    else -> 0
                }
                prefHelper.setCount(totalCount.toString())
                _mCountLiveData.postValue(countResponse.value)
            }
        }

        if (totalCount <= 0) {
            hideProgressBar()
            return@launch
        }

        // Step 2: Fetch all pages based on totalCount
        val pageSize = 10
        val fullPages = totalCount / pageSize
        val remainder = totalCount % pageSize
        val maxPage = if (remainder == 0) fullPages else fullPages + 1

        val accumulatedList = mutableListOf<PendingOrderData>()

        for (page in 1..maxPage) {
            val isLastPage = page == maxPage
            val thisPageSize = if (isLastPage && remainder != 0) remainder else pageSize

            val response = repository.pendingOrderListByCustomer(
                customerId,
                page.toString(),
                thisPageSize.toString()
            )

            when (response) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    break
                }
                is ResultWrapper.Success -> {
                    val data = response.value.data.orEmpty()
                    accumulatedList.addAll(data)
                    pendingOrderList = accumulatedList
                    prepareFilteredList()
                }
            }
        }

        hideProgressBar()
    }



    // Step 2:
    fun fetchCustomerToPendingDataInBackground() = viewModelScope.launch {
        val response = repository.count(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,
                pageNumber = "1",
                pageSize = "10"
            )
        )

        when (response) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                val count: Int = when (val data = response.value.data) {
                    is Int -> data
                    is Double -> data.toInt()
                    is Float -> data.toInt()
                    is String -> data.toDoubleOrNull()?.toInt() ?: 10
                    else -> 10
                }

                totalCount = count
                prefHelper.setCount(count.toString())
                _countText.postValue("$count records")
                _mCountLiveData.postValue(response.value)
                if (hasFetchedPage1) {
                    fetchRemainingPagesbyCustomer(2, count, pendingOrderList.toMutableList())
                }
            }
        }
    }


    // Step 3:
    private suspend fun fetchRemainingPagesbyCustomer(
        startPage: Int,
        totalCount: Int,
        accumulatedList: MutableList<PendingOrderData>
    ) {
        val fullPages = totalCount / itemsPerCall
        val remainder = totalCount % itemsPerCall
        val maxPage = if (remainder == 0) fullPages else fullPages + 1

        for (page in startPage..maxPage) {
            val isLastPage = page == maxPage
            val pageSize = if (isLastPage && remainder != 0) remainder else itemsPerCall

            val request = FilterRequest(
                buyerIDs = null,
                fromDate = null,
                supplierIDs = null,
                toDate = null,
                isSupplier = true,
                pageNumber = page.toString(),
                pageSize = pageSize.toString()
            )

            when (val response = repository.pendingOrderList(request)) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    return
                }
                is ResultWrapper.Success -> {
                    val data = response.value.data.orEmpty()
                    accumulatedList.addAll(data)

                    pendingOrderList = accumulatedList
                    prepareFilteredList()
                }
            }
        }
    }


    fun clearPendingListData() {
        pendingOrderList = emptyList()
        prepareFilteredList()
     //   widgetList = emptyList()
      //  _isListAvailable.postValue(Unit)  // triggers observer to refresh adapter
        _responseCodeOfPendingList.postValue("")
        responseMessageOfPendingList.postValue("")   }


    fun fetchOrdersPageByCustomerFirstPage(customerId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        val pageSize = 10
        val accumulatedList = mutableListOf<PendingOrderData>()

        // Step 1: First Page
        val firstPageResponse = repository.pendingOrderListByCustomer(customerId, "1", pageSize.toString())
        when (firstPageResponse) {
            is ResultWrapper.Failure -> {
                apiErrorData(firstPageResponse.error)
                hideProgressBar()
                return@launch
            }
            is ResultWrapper.Success -> {
                val data = firstPageResponse.value.data.orEmpty()

                if (data.isEmpty()) {
                    showToast("No Data Found")
                    // No data in first page -> clear list & show "No Data Found"
                    clearPendingListData()
                    hideProgressBar()
                    return@launch
                }

                accumulatedList.addAll(data)
                pendingOrderList = accumulatedList
                prepareFilteredList()
                hasFetchedPage1 = true
            }
        }

        hideProgressBar()

        // Step 2: Get total count and fetch remaining pages in background
        val countResponse = repository.countCustomer(customerId)
        when (countResponse) {
            is ResultWrapper.Failure -> apiErrorData(countResponse.error)
            is ResultWrapper.Success -> {
                val totalCount = when (val data = countResponse.value.data) {
                    is Int -> data
                    is Double -> data.toInt()
                    is Float -> data.toInt()
                    is String -> data.toDoubleOrNull()?.toInt() ?: 0
                    else -> 0
                }

                if (totalCount > pageSize) {
                    fetchRemainingPagesByCustomer(customerId, 2, totalCount, accumulatedList)
                }
            }
        }
    }

    private fun fetchRemainingPagesByCustomer(
        customerId: String,
        startPage: Int,
        totalCount: Int,
        accumulatedList: MutableList<PendingOrderData>
    ) = viewModelScope.launch {
        val fullPages = totalCount / itemsPerCall
        val remainder = totalCount % itemsPerCall
        val maxPage = if (remainder == 0) fullPages else fullPages + 1

        for (page in startPage..maxPage) {
            val isLastPage = page == maxPage
            val pageSize = if (isLastPage && remainder != 0) remainder else itemsPerCall

            val response = repository.pendingOrderListByCustomer(customerId, page.toString(), pageSize.toString())
            when (response) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    return@launch
                }
                is ResultWrapper.Success -> {
                    val data = response.value.data.orEmpty()
                    accumulatedList.addAll(data)
                    pendingOrderList = accumulatedList
                    prepareFilteredList()
                }
            }
        }
    }




}