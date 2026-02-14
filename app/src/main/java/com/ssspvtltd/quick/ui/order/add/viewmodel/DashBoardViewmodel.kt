package com.ssspvtltd.quick.ui.order.add.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.DashBoardDataResponse
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.model.version.CheckVersionResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.add.repositry.DashBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashBoardViewmodel @Inject constructor(
    private val gson: Gson,
    private val repository: DashBoardRepository
) : RecyclerWidgetViewModel() {

    private var _dashBoardDetailsLiveData = MutableLiveData<DashBoardDataResponse.Data?>()
    val dashBoardDetailsLiveData: LiveData<DashBoardDataResponse.Data?> = _dashBoardDetailsLiveData

    private val _dashBoardSaleCountDetailsLiveData = MutableLiveData<DashBoardDataResponse.Data?>()
    val dashBoardSaleCountDetailsLiveData: LiveData<DashBoardDataResponse.Data?> =
        _dashBoardSaleCountDetailsLiveData

    private var _needToUpdateVersion = MutableLiveData<CheckVersionResponse.Data?>()
    val fetchCheckVersion: MutableLiveData<CheckVersionResponse.Data?> = _needToUpdateVersion

    companion object {
        const val TAG = "TaG"
    }

    fun getDashBoardDetails() = viewModelScope.launch {

        when (val response = repository.callGetDashBoardDetails(prefHelper.getAccountId() ?: "")) {

            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                val dashBoardData: DashBoardDataResponse.Data? = response.value.body()?.data
                Log.i(TAG, "getDashBoardDetails: $dashBoardData")
                withContext(Dispatchers.Main) {
                    _dashBoardDetailsLiveData.postValue(dashBoardData)
                }

            }
        }


    }
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    fun getDashBoardSaleCountDetails(fromDate: String?, toDate: String?) =
        viewModelScope.launch {

            _loading.value = true   // Show progress

            val response = withContext(Dispatchers.IO) {
                repository.callGetDashBoardSaleCountDetails(
                    prefHelper.getAccountId() ?: "",
                    fromDate,
                    toDate
                )
            }

            _loading.value = false  // Hide progress

            when (response) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                }

                is ResultWrapper.Success -> {
                    _dashBoardSaleCountDetailsLiveData.value =
                        response.value.body()?.data
                }
            }
        }


    fun checkVersionAPI(appName: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Checking Version..."))
        when (val response = repository.checkVersion(appName)) {
            is ResultWrapper.Failure -> {
                println("ERROR_IN_RESPONSE ${response.error}")
                apiErrorData(response.error)
            }

            is ResultWrapper.Success -> {
                hideProgressBar()
                println("ERROR_IN_RESPONSE 2 ${response.value.body()?.data}")
                _needToUpdateVersion.postValue(response.value.body()?.data)
            }
        }
    }

}