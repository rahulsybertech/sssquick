package com.ssspvtltd.quick_new.ui.order.add.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick_new.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_new.model.DashBoardDataResponse
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.ui.order.add.repositry.DashBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashBoardViewmodel @Inject constructor(
    private val gson: Gson,
    private val repository: DashBoardRepository
): RecyclerWidgetViewModel() {

    private var _dashBoardDetailsLiveData = MutableLiveData<DashBoardDataResponse.Data?>()
    val dashBoardDetailsLiveData: LiveData<DashBoardDataResponse.Data?> = _dashBoardDetailsLiveData

    private val _dashBoardSaleCountDetailsLiveData = MutableLiveData<DashBoardDataResponse.Data?>()
    val dashBoardSaleCountDetailsLiveData: LiveData<DashBoardDataResponse.Data?> = _dashBoardSaleCountDetailsLiveData

    /*private val _combinedDashBoardLiveData = MediatorLiveData<DashBoardDataResponse.Data?>()
    val combinedDashBoardLiveData: LiveData<DashBoardDataResponse.Data?> = _combinedDashBoardLiveData

    init {
        // Observe dashBoardDetailsLiveData and dashBoardSaleCountDetailsLiveData
        _combinedDashBoardLiveData.addSource(_dashBoardDetailsLiveData) { detailsData ->
            detailsData?.totalSaleCount  = _dashBoardSaleCountDetailsLiveData.value?.totalSaleCount
        }

    }*/

    companion object{
        const val TAG = "TaG"
    }

    fun getDashBoardDetails() = viewModelScope.launch{

        when( val response = repository.callGetDashBoardDetails(prefHelper.getAccountId() ?: "")) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                val dashBoardData: DashBoardDataResponse.Data? = response.value.data?.data
                Log.i(TAG, "getDashBoardDetails: $response")
                withContext(Dispatchers.Main) {
                    _dashBoardDetailsLiveData.postValue(dashBoardData)
                }

            }
        }


    }

    fun getDashBoardSaleCountDetails(fromDate: String, toDate: String) = viewModelScope.launch{

        when( val response = repository.callGetDashBoardSaleCountDetails(prefHelper.getAccountId() ?: "", fromDate, toDate)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                val dashBoardData: DashBoardDataResponse.Data? = response.value.data?.data
                Log.i(TAG, "getDashBoardSaleCountDetails: $dashBoardData")
                withContext(Dispatchers.Main) {
                    _dashBoardDetailsLiveData.postValue(dashBoardData)
                }

            }
        }


    }
}