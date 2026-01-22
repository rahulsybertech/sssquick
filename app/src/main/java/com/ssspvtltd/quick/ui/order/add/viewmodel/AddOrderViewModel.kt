package com.ssspvtltd.quick.ui.order.add.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.constants.ButtonType
import com.ssspvtltd.quick.constants.CheckInType
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.GoodsReturnItem
import com.ssspvtltd.quick.model.SubPartyGRData
import com.ssspvtltd.quick.model.TransportMasterData
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.order.add.DispatchTypeList
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.VoucherData
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.add.repositry.AddOrderRepositry
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _salePartyDetailNew =
        MutableLiveData<com.ssspvtltd.quick.model.order.add.salepartyNewList.Data?>()
    val salePartyDetailNew: MutableLiveData<com.ssspvtltd.quick.model.order.add.salepartyNewList.Data?> get() = _salePartyDetailNew

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

    private val _getDispatchTypeList = MutableLiveData<List<DispatchTypeList>>()
    val getDispatchTypeList: LiveData<List<DispatchTypeList>> get() = _getDispatchTypeList

    // Added Item List
    var addItemDataList = ArrayList<PackType>()

    var addImageDataList = ArrayList<ImageModel>()
    private var mainList = listOf<CustomerData>()

    private val _customerDataResp = MutableLiveData<List<CustomerData>>()
    val customerDataResp: LiveData<List<CustomerData>> get() = _customerDataResp

    /**
     * Initializes all data required for adding an order.
     * Determines whether to start a new order or edit an existing one based on the pendingOrderID.
     */
    fun initAllData(nickNameId: String?, schemeId: String?, type: Boolean) {

        if (pendingOrderID.isNullOrBlank()) {
            initializeNewOrder(nickNameId, schemeId, type)
        } else {
            initializeEditOrder(schemeId, type)
        }
    }

    /**
     * Starts the initialization process for a new order.
     * Fetches voucher, sale party, scheme, and purchase party data asynchronously.
     */
    private fun initializeNewOrder(nickNameId: String?, schemeId: String?, type: Boolean) =
        viewModelScope.launch(Dispatchers.Default) {
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

            val voucherDeferred = async { getVoucherSuspend() }
            val salePartyDeferred = async { getSalePartySuspend() }
            val dispatchTypeListDeferred = async { getGetDispatchTypeListSuspend() }
            val schemeDeferred = async { getSchemeSuspend() }

            // Await responses of faster APIs
            val voucherResponse = voucherDeferred.await()
            val salePartyResponse = salePartyDeferred.await()
            val schemeResponse = schemeDeferred.await()
            val dispatchTypeListResponse = dispatchTypeListDeferred.await()

            _voucherData.postValue(voucherResponse)
            _saleParty.postValue(salePartyResponse)
            _scheme.postValue(schemeResponse)
            _getDispatchTypeList.postValue(dispatchTypeListResponse)


            hideProgressBar()
        }


    fun initAllDataGr(pendingOrderId: String?) {

        if (pendingOrderId.isNullOrBlank()) {
            initializeCreateGR()
        } else {
            initializeEditOrderGr("schemeId", true)
        }
    }
    fun initializeCreateGR() = viewModelScope.launch(Dispatchers.Default) {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        val voucherDeferred = async { getVoucherSuspend() }

        val voucherResponse = voucherDeferred.await()


        _voucherData.postValue(voucherResponse)


        hideProgressBar()
    }

    fun getCustomer(isProgressVisible: Boolean) = viewModelScope.launch {
        if (isProgressVisible)
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        else {
            hideProgressBar()
        }
        // Log.e("respo", repository.customerList().toString())
        when (val response = repository.customerList()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    val tabType = when {
                        it.any { data -> data.checkInType == CheckInType.ORDER_IN_MARKET.value } -> CheckInType.ORDER_IN_MARKET
                        it.any { data -> data.checkInType == CheckInType.WITH_MARKETER.value } -> CheckInType.WITH_MARKETER
                        it.any { data -> data.checkInType == CheckInType.ORDER_IN_OFFICE.value } -> CheckInType.ORDER_IN_OFFICE
                        else -> if (response.value.remark != null) {
                            CheckInType.OTHER
                        } else {
                            CheckInType.ORDER_IN_OFFICE
                        }
                    }
                    mainList = it.filter { it.accountCode.isNotNullOrBlank() }
                    //   unmodifiedMainList = ArrayList(mainList)
                    // _isListModified.postValue(false)
                    //  validateUpdateButton()
                    // selectedItem = it.filter { it.chkStatus == true }
                    prefHelper.setCheckinStatus(response.value.checkinStatus ?: false)
                    val btnType =
                        if (response.value.checkinStatus == true) ButtonType.CHECK_OUT else ButtonType.CHECK_IN
                    // val btnType = if (selectedItem.isEmpty()) ButtonType.CHECK_IN else ButtonType.CHECK_OUT
                    _customerDataResp.postValue(mainList)
                    clearWidgetList()
                    addItemToWidgetList(mainList)
                    withContext(Dispatchers.Main) {
                        /*  _selectedTab.postValue(tabType)
                        _buttonType.postValue(btnType)
                        _setRemark.postValue(response.value.remark.toString())
                        remark = response.value.remark.toString()
                        apiRemark = remark*/
                        listDataChanged()
                        hideProgressBar()
                    }
                }
            }
        }
    }

    private fun initializeEditOrder(schemeId: String?, type: Boolean) =
        viewModelScope.launch(Dispatchers.Default) {

       //     val salePartyDeferred = async { getSalePartySuspend() }
            val schemeDeferred = async { getSchemeSuspend() }
            //  val purchasePartyDeferred   = async { getPurchasePartySuspend(schemeId, type) }

         //   val salePartyResponse = salePartyDeferred.await()
            val schemeResponse = schemeDeferred.await()
            // val purchasePartyResponse   = purchasePartyDeferred.await()

            /*_salePartyEdit.postValue(salePartyResponse)
        _schemeEdit.postValue(schemeResponse)
        _purchasePartyEdit.postValue(purchasePartyResponse)*/

       //     _saleParty.postValue(salePartyResponse)
            _scheme.postValue(schemeResponse)
            //  _purchaseParty.postValue(purchasePartyResponse)
            _setEditOrderFields.postValue(true)

        }
    private fun initializeEditOrderGr(schemeId: String?, type: Boolean) =
        viewModelScope.launch(Dispatchers.Default) {
            _setEditOrderFields.postValue(true)

        }
    fun getVoucher() = viewModelScope.launch {
        //  showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        getVoucherSuspend()?.let {
            _voucherData.postValue(it)
            hideProgressBar()
        }
    }

    private suspend fun getVoucherSuspend(): VoucherData? = withContext(Dispatchers.Default) {
        return@withContext when (val response = repository.getVoucher()) {
            is ResultWrapper.Success -> {
                response.value.data
            }

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


    private suspend fun getGetDispatchTypeListSuspend(): List<DispatchTypeList> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response = repository.getGetDispatchTypeListList()) {
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
    private val _purchaseParty_ = MutableLiveData<List<PurchasePartyData>?>()
    val purchaseParty_: LiveData<List<PurchasePartyData>?> get() = _purchaseParty_
    fun getPurchaseParty(nickNameId: String?, schemeId: String?, type: Boolean) =
        viewModelScope.launch {
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
            getPurchasePartySuspend(nickNameId, schemeId, type).let {
                _purchaseParty_.postValue(it)
                hideProgressBar()
            }
        }


    fun getPurchasePartyInGr(nickNameId: String?, schemeId: String?, type: Boolean) =
        viewModelScope.launch {
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
            getPurchasePartySuspendInGr(nickNameId, schemeId, type).let {
                _purchaseParty_.postValue(it)
                hideProgressBar()
            }
        }

    private val _nickNameList = MutableLiveData<List<PurchasePartyData>?>()
    val nickNameList: LiveData<List<PurchasePartyData>?> get() = _nickNameList

    fun getNickNameList(nickNameId: String?, schemeId: String?, type: Boolean) =
        viewModelScope.launch {
            //   showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
            getPurchasePartyListWithSuplierSuspend(nickNameId, schemeId, type).let {
                _nickNameList.postValue(it)
                hideProgressBar()
            }
        }

    
    private val _purchasePartyByNickName = MutableLiveData<List<PurchasePartyData>?>()
    val purchasePartyByNickName: LiveData<List<PurchasePartyData>?> get() = _purchasePartyByNickName

    fun getPurchasePartyByNickName(schemeId: String?) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        getPurchasePartyByNickNameSuspend(schemeId).let {
            _purchasePartyByNickName.postValue(it)
            hideProgressBar()
        }
    }

    fun getInitialPurchaseParty(nickNameId: String?, schemeId: String?, type: Boolean) =
        viewModelScope.launch(Dispatchers.Default) {
            val purchasePartyDeferred = async {
                getPurchasePartySuspend(nickNameId, schemeId, type)
            }
            val purchasePartyResponse = purchasePartyDeferred.await()
            _purchaseParty.postValue(purchasePartyResponse)
        }

    private suspend fun getPurchasePartySuspend(
        nickNameId: String?,
        schemeId: String?,
        type: Boolean
    ): List<PurchasePartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response =
                repository.purchasePartyList(nickNameId, schemeId, type)) {
                is ResultWrapper.Success -> {
                    response.value.data.orEmpty()
                }

                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }
    private suspend fun getPurchasePartySuspendInGr(
        nickNameId: String?,
        schemeId: String?,
        type: Boolean
    ): List<PurchasePartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response =
                repository.purchasePartyListInGr(nickNameId, schemeId, type)) {
                is ResultWrapper.Success -> {
                    response.value.data.orEmpty()
                }

                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }

    private suspend fun getPurchasePartyListWithSuplierSuspend(
        nickNameId: String?,
        schemeId: String?,
        type: Boolean
    ): List<PurchasePartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response =
                repository.purchasePartyListWithSuplier(nickNameId, schemeId, type)) {
                is ResultWrapper.Success -> {
                    response.value.data.orEmpty()
                }

                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }

    private suspend fun getPurchasePartyByNickNameSuspend(nickNameId: String?): List<PurchasePartyData> {
        return withContext(Dispatchers.Default) {
            return@withContext when (val response =
                repository.purchasePartyListByNickName(nickNameId)) {
                is ResultWrapper.Success -> {
                    response.value.data.orEmpty()
                }

                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    listOf() // return empty list
                }
            }
        }
    }


    fun getStation(salePartyId: String, subPartyId: String) = viewModelScope.launch {
        /*      showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
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
        }*/
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

    fun getSalePartyDetailsNew(accountId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.salePartyDetailNew(accountId)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                Log.e("ersss", response.toString())
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    withContext(Dispatchers.Main) {
                        _salePartyDetailNew.postValue(response.value.data)
                        hideProgressBar()
                    }
                }
            }
        }
    }

    fun getSalePartyAndStationData(accountId: String, salePartyId: String, subPartyId: String) =
        viewModelScope.launch {
            showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
            val job1 = viewModelScope.launch(Dispatchers.IO) {
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


            /*    val job2 = viewModelScope.launch (Dispatchers.IO) {
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
                    }*/


            job1.join()
            //  job2.join()
            hideProgressBar()
        }


    private val _subPartyDetailGR = MutableLiveData<List<SubPartyGRData>?>()
    val subPartyDetailGR: MutableLiveData<List<SubPartyGRData>?> get() = _subPartyDetailGR
    fun getSubPartyGR(accountId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        val job1 = viewModelScope.launch(Dispatchers.IO) {
            when (val response = repository.subPartyDetailGR(accountId)) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    Log.e("ersss", response.toString())
                }

                is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                    response.value.data?.let {
                        withContext(Dispatchers.Main) {
                            _subPartyDetailGR.postValue(response.value.data)
                        }
                    }
                }
            }

        }


        /*    val job2 = viewModelScope.launch (Dispatchers.IO) {
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
                        }*/


        job1.join()
        //  job2.join()
        hideProgressBar()
    }


    /*    private val _transPortDetailGR = MutableLiveData<List<TransportMasterData>?>()
    val transPortDetailGR: MutableLiveData<List<TransportMasterData>?> get() = _transPortDetailGR
    fun getTransportGRParam(text: String ) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        val job1  = viewModelScope.launch (Dispatchers.IO) {
            when (val response = repository.tranportDetailsGRReq(text)) {
                is ResultWrapper.Failure -> {
                    apiErrorData(response.error)
                    Log.e("ersss", response.toString())
                }

                is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                    response.value.data?.let {
                        withContext(Dispatchers.Main) {
                            _transPortDetailGR.postValue(response.value.data)
                        }
                    }
                }
            }

        }
        job1.join()
        //  job2.join()
        hideProgressBar()
    }*/

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _transPortDetailGR = MutableStateFlow<List<TransportMasterData>>(emptyList())
    val transPortDetailGR: StateFlow<List<TransportMasterData>> = _transPortDetailGR

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    init {
        // Load default data (e.g., top 10 or all transport data)
        viewModelScope.launch {
            fetchTransportData("") // empty query for default
        }

        viewModelScope.launch {
            searchQuery
                .debounce(300)
               /* .filt
               er { it.length >= 1 }*/
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchTransportData(query)
                }
        }
    }



    private suspend fun fetchTransportData(query: String) {
       // showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))

        when (val response = repository.tranportDetailsGRReq(query)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                Log.e("API_ERROR", response.toString())
            }

            is ResultWrapper.Success -> {
                response.value.data?.let {
                    _transPortDetailGR.value = it
                }
            }
        }
    }




    fun placeOrder(params: HashMap<String, RequestBody?>) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Please wait...")) // ✅ Show progress before API call

        try {
            params["OrderBookSecondaryList"] = gson.toJson(addItemDataList).toRequestBody()
            val documents = mutableListOf<MultipartBody.Part>()

            addImageDataList.forEach { imageModel ->
                val file = imageModel.filePath?.let { File(it) }
                if (file?.exists() == true) {
                    MultipartBody.Part.createFormData(
                        "documents", file.name,
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    ).let { documents.add(it) }
                }
            }

            println("GETTING_REQUEST_PLACE_ORDER ${Gson().toJson(params)}")

            // 🔹 Move network call to IO Dispatcher (background thread)
            val response = withContext(Dispatchers.IO) {
                repository.placeOrder(params, documents)
            }

            // 🔹 Handle API response on Main thread
            when (response) {
                is ResultWrapper.Failure -> {
                    hideProgressBar() // ✅ Hide progress bar on failure
                    if (response.error.islimitexceed == true) {
                        println("getSalePartyAndStationData - ${response.error.message!!}")
                        _isOrderPlacedLimitError.postValue(response.error.message!!)
                    } else {
                        apiErrorData(response.error, getString(R.string.error))
                    }
                }
                is ResultWrapper.Success -> {
                    hideProgressBar() // ✅ Hide progress bar on success
                    println("getSalePartyAndStationData 2 - ${response.value.message.toString()}")

                    _isOrderPlaced.postValue(true)
                    _isOrderPlacedSuccess.postValue(response.value.message.toString())
                    Log.e("documents", gson.toJson(documents))
                }
            }
        } catch (e: Exception) {
            hideProgressBar() // ✅ Ensure progress bar is hidden in case of an exception
            Log.e("API_ERROR", "Error placing order: ${e.message}")
        }
    }

    fun placeOrderGr(params: HashMap<String, RequestBody?>) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Please wait..."))
        _isOrderPlaced.postValue(true)
        try {
            // ✅ Convert addItemDataList to GoodsReturnItemList
            val addItemDataList1 = mutableListOf<GoodsReturnItem>()
            addItemDataList.forEach { pack ->
                pack.itemDetail?.forEach { item ->
                    if (!item.itemID.isNullOrBlank() && !item.itemName.isNullOrBlank() && !item.itemQuantity.isNullOrBlank()) {
                        addItemDataList1.add(
                            GoodsReturnItem(
                                id = pack.id,
                                itemId = item.itemID!!,
                                itemName = item.itemName!!,
                                itemQty = item.itemQuantity!!.toIntOrNull() ?: 0
                            )
                        )
                    }
                }
            }

            // ✅ Add JSON to params
            params["StrGoodsReturnItemList"] = gson
                .toJson(addItemDataList1)
                .toRequestBody("text/plain".toMediaTypeOrNull())

            // ✅ Attach documents
            val documents = mutableListOf<MultipartBody.Part>()
            addImageDataList.forEach { imageModel ->
                val file = imageModel.filePath?.let { File(it) }
                if (file?.exists() == true) {
                    val part = MultipartBody.Part.createFormData(
                        "ItemDetailFiles", file.name,
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    documents.add(part)
                }
            }

            // ✅ Send API request
            val response = withContext(Dispatchers.IO) {
                repository.placeOrderGr(params, documents)
            }

            when (response) {
                is ResultWrapper.Failure -> {
                    hideProgressBar()
                    apiErrorData(response.error, "Error")
                }
                is ResultWrapper.Success -> {
                    hideProgressBar()
                    _isOrderPlaced.postValue(true)
                    _isOrderPlacedSuccess.postValue(response.value.message ?: "Success")
                }
            }

        } catch (e: Exception) {
            hideProgressBar()
            Log.e("API_ERROR", "placeOrderGr Exception: ${e.message}")
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
                println("getEditOrderDataIfNeeded ${response.value.data?.transportName?:""}")
                Log.i("getEditOrderDataIfNeeded", response.value.data?.transportName?:"")
                response.value.data?.let {
                    withContext(Dispatchers.Main) {

                        _editOrderData.postValue(response.value.data)
                        hideProgressBar()
                    }
                }
            }
        }
    }


    private val _editOrderDataGr = MutableLiveData<GoodsReturnDataGr?>()
    val editOrderDataGr: LiveData<GoodsReturnDataGr?> get() = _editOrderDataGr
    fun getEditOrderDataIfNeededGr(pendingOrderID:String) = viewModelScope.launch {
        if (pendingOrderID.isNullOrBlank()) return@launch
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        //initializeEditOrder()
        when (val response = repository.editOrderGr(pendingOrderID)) {
            is ResultWrapper.Failure -> {
                apiErrorData(response.error)
                Log.e("getEditOrderDataIfNeeded", response.toString())
            }

            is ResultWrapper.Success -> withContext(Dispatchers.Default) {
                response.value.data?.let {
                    withContext(Dispatchers.Main) {
                        _editOrderDataGr.postValue(response.value.data)
                        hideProgressBar()
                    }
                }
            }
        }
    }
}
