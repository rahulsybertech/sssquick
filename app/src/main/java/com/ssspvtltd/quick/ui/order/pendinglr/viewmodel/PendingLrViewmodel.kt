package com.ssspvtltd.quick.ui.order.pendinglr.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel

import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.IntResponse
import com.ssspvtltd.quick.model.auth.LoginData
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.add.viewmodel.DashBoardViewmodel.Companion.TAG
import com.ssspvtltd.quick.ui.order.pendinglr.repository.PendingLrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class PendingLrViewmodel @Inject constructor(
    private val repository: PendingLrRepository
) : RecyclerWidgetViewModel() {


    private var pendingLrList = listOf<PendingLrData>()
 /*   private val _mCountLiveData = MutableLiveData<Ap?>()
    val mCountLiveData: LiveData<IntResponse?> get() = _mCountLiveData
*/
    private val _mCountLiveData = MutableLiveData<ApiResponse<*>?>()
    val mCountLiveData: LiveData<ApiResponse<*>?> get() = _mCountLiveData


    private var mCount=""
    var searchValue = ""
   /* fun getPendingLr() = viewModelScope.launch {
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

    fun getCount() = viewModelScope.launch {

        when (val response = repository.count(    GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null
        ))) {

            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                prefHelper.setCount(response.value.data.toString())
                withContext(Dispatchers.Main) {
                    hideProgressBar()
                    _mCountLiveData.postValue(response.value)
                }

            }
        }


    }*/

    private val accumulatedPendingLrList = mutableListOf<PendingLrData>()
    private var hasFetchedFirstPageOfLr = false
    private var totalPendingLrCount: Int? = null
    private val itemsPerPage = 10
   // private var mCount = ""
  //  var searchValue = ""

    // 🔹 Call this to start everything
    fun startFetchingPendingLr() {
        fetchPendingLrPage(1)                // Fetch page 1 immediately
        fetchPendingLrCountInBackground()    // Start count in parallel
    }

    // Step 1: Fetch Page 1
    private fun fetchPendingLrPage(page: Int) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        val request = GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null,
            pageNumber = page.toString(),
            pageSize = itemsPerPage.toString()
        )

        when (val response = repository.pendingLrList(request)) {
            is ResultWrapper.Failure -> {
                hideProgressBar()
                apiErrorData(response.error)
            }
            is ResultWrapper.Success -> {
                val data = response.value.data.orEmpty()
                accumulatedPendingLrList.clear()
                accumulatedPendingLrList.addAll(data)
                pendingLrList = accumulatedPendingLrList
                prepareFilteredList()
                hasFetchedFirstPageOfLr = true

                // If count is already available, continue pagination
                totalPendingLrCount?.let {
                    fetchRemainingPendingLrPages(2, it)
                }
            }
        }
    }
    private val _countText = MutableLiveData<String>()
    val countText: LiveData<String> = _countText
    // Step 2: Fetch Count in Parallel
    private fun fetchPendingLrCountInBackground() = viewModelScope.launch {
        val request = GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null,"1","10"
        )

        when (val response = repository.count(request)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
               // val count = ((response.value.data) as? Int) ?: 10
                val count: Int = when (val data = response.value.data) {
                    is Int -> data
                    is Double -> data.toInt()
                    is Float -> data.toInt()
                    is String -> data.toDoubleOrNull()?.toInt() ?: 10
                    else -> 10
                }
                totalPendingLrCount = count
                mCount = count.toString()
                prefHelper.setCount(mCount)
                _countText.postValue("$count records")
                _mCountLiveData.postValue(response.value)

                // If first page already fetched, continue from page 2
                if (hasFetchedFirstPageOfLr) {
                    fetchRemainingPendingLrPages(2, count)
                }
            }
        }
    }

    // Step 3: Fetch Remaining Pages after count
    private fun fetchRemainingPendingLrPages(
        startPage: Int,
        totalCount: Int
    ) = viewModelScope.launch {
        val basePageSize = 10
        val maxPage = (totalCount + basePageSize - 1) / basePageSize

        for (page in startPage..maxPage) {
            val pageSize = page * basePageSize  // Page 2 = 20, Page 3 = 30, ...

            val request = GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,
                pageNumber = page.toString(),
                pageSize = pageSize.toString()
            )

            when (val response = repository.pendingLrList(request)) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    return@launch
                }
                is ResultWrapper.Success -> {
                    val data = response.value.data.orEmpty()
                    accumulatedPendingLrList.addAll(data)
                    pendingLrList = accumulatedPendingLrList
                    prepareFilteredList()
                }
            }
        }

        hideProgressBar()
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