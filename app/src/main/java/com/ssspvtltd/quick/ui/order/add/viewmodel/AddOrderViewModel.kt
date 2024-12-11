package com.ssspvtltd.quick.ui.order.add.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.VoucherData
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.add.repositry.AddOrderRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AddOrderViewModel @Inject constructor(
    private val gson: Gson,
    private val repository: AddOrderRepositry,
    savedStateHandle: SavedStateHandle
) : RecyclerWidgetViewModel() {

    val pendingOrderID = savedStateHandle.get<String>(ARG_PENDING_ORDER_ID)

    var schemeIdId: String? = null

    private val _voucherData = MutableLiveData<VoucherData?>()
    val voucherData: LiveData<VoucherData?> get() = _voucherData

    private val _saleParty = MutableLiveData<List<SalepartyData>>()
    val saleParty: LiveData<List<SalepartyData>> get() = _saleParty

    private val _station = MutableLiveData<List<AllStation>>()
    val station: LiveData<List<AllStation>> get() = _station

    private val _salePartyDetail = MutableLiveData<Data?>()
    val salePartyDetail: MutableLiveData<Data?> get() = _salePartyDetail

    private val _scheme = MutableLiveData<List<SchemeData>>()
    val scheme: LiveData<List<SchemeData>> get() = _scheme

    private val _purchaseParty = MutableLiveData<List<PurchasePartyData>?>()
    val purchaseParty: LiveData<List<PurchasePartyData>?> get() = _purchaseParty

    private val _isOrderPlaced = MutableLiveData<Boolean>()
    val isOrderPlaced: LiveData<Boolean> get() = _isOrderPlaced

    private val _isOrderPlacedSuccess = MutableLiveData<String>()
    val isOrderPlacedSuccess: LiveData<String> get() = _isOrderPlacedSuccess

    private val _isOrderPlacedLimitError = MutableLiveData<String>()
    val isOrderPlacedLimitError: LiveData<String> get() = _isOrderPlacedLimitError

    private val _editOrderData = MutableLiveData<EditOrderDataNew?>()
    val editOrderData: LiveData<EditOrderDataNew?> get() = _editOrderData


    //=================================== For Edit Order ===========================================
    private val _salePartyEdit = MutableLiveData<List<SalepartyData>>()
    val salePartyEdit: LiveData<List<SalepartyData>> get() = _salePartyEdit

    private val _schemeEdit = MutableLiveData<List<SchemeData>>()
    val schemeEdit: LiveData<List<SchemeData>> get() = _schemeEdit

    private val _purchasePartyEdit = MutableLiveData<List<PurchasePartyData>?>()
    val purchasePartyEdit: LiveData<List<PurchasePartyData>?> get() = _purchasePartyEdit

    private val _setEditOrderFields = MutableLiveData<Boolean>()
    val setEditOrderFields: LiveData<Boolean> get() = _setEditOrderFields

    private val _salePartyDetailEdit = MutableLiveData<Data?>()
    val salePartyDetailEdit: MutableLiveData<Data?> get() = _salePartyDetailEdit


    // Added Item List
    var addItemDataList = ArrayList<PackType>()
    var addImageDataList = ArrayList<ImageModel>()

    /**
     * Initializes all data required for adding an order.
     * Determines whether to start a new order or edit an existing one based on the pendingOrderID.
     */
    fun initAllData() {

        if (pendingOrderID.isNullOrBlank()) {
            initializeNewOrder()
        } else {
            initializeEditOrder()
        }
    }

    /**
     * Starts the initialization process for a new order.
     * Fetches voucher, sale party, scheme, and purchase party data asynchronously.
     */
    private fun initializeNewOrder() = viewModelScope.launch(Dispatchers.Default) {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        val voucherDeferred         = async { getVoucherSuspend() }
        val salePartyDeferred       = async { getSalePartySuspend() }
        val schemeDeferred          = async { getSchemeSuspend() }
        val purchasePartyDeferred   = async { getPurchasePartySuspend(null) }

        val voucherResponse         = voucherDeferred.await()
        val salePartyResponse       = salePartyDeferred.await()
        val schemeResponse          = schemeDeferred.await()
        val purchasePartyResponse   = purchasePartyDeferred.await()

        _voucherData.postValue(voucherResponse)
        _saleParty.postValue(salePartyResponse)
        _scheme.postValue(schemeResponse)
        _purchaseParty.postValue(purchasePartyResponse)

        hideProgressBar()
    }

    private fun initializeEditOrder() = viewModelScope.launch(Dispatchers.Default) {

        val salePartyDeferred       = async { getSalePartySuspend() }
        val schemeDeferred          = async { getSchemeSuspend() }
        val purchasePartyDeferred   = async { getPurchasePartySuspend(null) }

        val salePartyResponse       = salePartyDeferred.await()
        val schemeResponse          = schemeDeferred.await()
        val purchasePartyResponse   = purchasePartyDeferred.await()

        /*_salePartyEdit.postValue(salePartyResponse)
        _schemeEdit.postValue(schemeResponse)
        _purchasePartyEdit.postValue(purchasePartyResponse)*/

        _saleParty.postValue(salePartyResponse)
        _scheme.postValue(schemeResponse)
        _purchaseParty.postValue(purchasePartyResponse)
        _setEditOrderFields.postValue(true)

    }

    fun getVoucher() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        getVoucherSuspend()?.let {
            _voucherData.postValue(it)
            hideProgressBar()
        }
    }

    private suspend fun getVoucherSuspend(): VoucherData? = withContext(Dispatchers.Default) {
        return@withContext when (val response = repository.getVoucher()) {
            is ResultWrapper.Success -> response.value.data
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                null
            }
        }
    }

    private suspend fun getSalePartySuspend(): List<SalepartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response = repository.salePartyList()) {
                is ResultWrapper.Success -> response.value.data.orEmpty()
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }

    private suspend fun getSchemeSuspend(): List<SchemeData> = withContext(Dispatchers.Default) {
        return@withContext when (val response = repository.scheme()) {
            is ResultWrapper.Success -> response.value.data.orEmpty()
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                listOf() // return empty list
            }
        }
    }

    fun getPurchaseParty(schemeId: String? = null) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        getPurchasePartySuspend(schemeId).let {
            _purchaseParty.postValue(it)
            hideProgressBar()
        }
    }
    fun getInitialPurchaseParty() = viewModelScope.launch(Dispatchers.Default) {
        val purchasePartyDeferred = async { getPurchasePartySuspend(null) }
        val purchasePartyResponse = purchasePartyDeferred.await()
        _purchaseParty.postValue(purchasePartyResponse)
    }

    private suspend fun getPurchasePartySuspend(schemeId: String? = null): List<PurchasePartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response = repository.purchasePartyList(schemeId)) {
                is ResultWrapper.Success -> response.value.data.orEmpty()
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }


    fun getStation(salePartyId: String, subPartyId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.allStationList(salePartyId, subPartyId)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    withContext(Dispatchers.Main) {
                        _station.postValue(it)
                        hideProgressBar()
                    }
                }
            }
        }
    }

    fun getSalePartyDetails(accountId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.salePartyDetail(accountId)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                Log.e("ersss", response.toString())
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    withContext(Dispatchers.Main) {
                        _salePartyDetail.postValue(response.value.data)
                        hideProgressBar()
                    }
                }
            }
        }
    }

    fun getSalePartyAndStationData(accountId: String, salePartyId: String, subPartyId: String ) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        val job1  = viewModelScope.launch (Dispatchers.IO) {
                        when (val response = repository.salePartyDetail(accountId)) {
                            is ResultWrapper.Failure -> {
                                apiErrorData(response.error)
                                Log.e("ersss", response.toString())
                            }

                            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                                response.value.data?.let {
                                    withContext(Dispatchers.Main) {
                                        _salePartyDetail.postValue(response.value.data)
                                    }
                                }
                            }
                        }

                    }


        val job2 = viewModelScope.launch (Dispatchers.IO) {
                        when (val response = repository.allStationList(salePartyId, subPartyId)) {
                            is ResultWrapper.Failure -> apiErrorData(response.error)
                            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                                response.value.data?.let {
                                    withContext(Dispatchers.Main) {
                                        _station.postValue(it)
                                    }
                                }
                            }
                        }
                    }


        job1.join()
        job2.join()
        hideProgressBar()
    }

    fun placeOrder(params: HashMap<String, RequestBody?>) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Please wait..."))
        params["OrderBookSecondaryList"] = gson.toJson(addItemDataList).toRequestBody()
        val documents = mutableListOf<MultipartBody.Part>()
        addImageDataList.forEach { imageModel ->
            val file = imageModel.filePath?.let { File(it) }
            if (file?.exists() == true) {
                MultipartBody.Part.createFormData(
                    "documents", file.getName(),
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                ).let { documents.add(it) }
            }
        }
        when (val response = repository.placeOrder(params, documents)) {
            is ResultWrapper.Failure -> {
                if (response.error.islimitexceed == true) {
                    println("getSalePartyAndStationData - ${response.error.message!!}")
                    hideProgressBar()
                    _isOrderPlacedLimitError.postValue(response.error.message!!)
                } else {
                    apiErrorData(response.error,getString((R.string.error)))
                }
            }
            is ResultWrapper.Success -> {
                println("getSalePartyAndStationData 2 - ${response.value.message.toString()}")
                _isOrderPlaced.postValue(true)
                _isOrderPlacedSuccess.postValue(response.value.message.toString())
                hideProgressBar()
                Log.e("documents", gson.toJson(documents))
                //showToast(response.value.message)

            }
        }
    }

    fun getEditOrderDataIfNeeded() = viewModelScope.launch {
        if (pendingOrderID.isNullOrBlank()) return@launch
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        //initializeEditOrder()
        when (val response = repository.editOrder(pendingOrderID)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                Log.e("getEditOrderDataIfNeeded", response.toString())
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                Log.i("getEditOrderDataIfNeeded", response.toString())
                response.value.data?.let {
                    withContext(Dispatchers.Main) {

                        _editOrderData.postValue(response.value.data)
                        hideProgressBar()
                    }
                }
            }
        }
    }
}
