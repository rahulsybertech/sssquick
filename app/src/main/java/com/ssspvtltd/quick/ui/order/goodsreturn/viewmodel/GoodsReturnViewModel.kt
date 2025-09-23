package com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.TitleSubtitleWrapper
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.GetStockInOfficeOrderDetailsRequest
import com.ssspvtltd.quick.model.GoodsReturnImageUploadRequest
import com.ssspvtltd.quick.model.grNew.GoodsReturnDataNew
import com.ssspvtltd.quick.model.grNew.GoodsReturnItem
import com.ssspvtltd.quick.model.grNew.GoodsReturnResponse
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ApiRequestCode
import com.ssspvtltd.quick.networking.ApiResponse
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.networking.safeApiCall
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
    private var goodsReturnDetailList = listOf<GoodsReturnDataNew>()

    private var originalList = listOf<GoodsReturnDataNew>()
    var searchValue = ""
    fun getGoodsReturn() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getGoodsReturn(
            GetStockInOfficeOrderDetailsRequest(
            buyerIDs = listOf(),
            fromDate = null,
            isSupplier = true,
            supplierIDs = listOf(),
            toDate = null,"1","50"
        )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                goodsReturnList = response.value.data.orEmpty()
                prepareFilteredList()
            }
        }
    }

    fun getGoodsReturnSecondary(id:String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.getGoodsReturnSecondary(
            GetStockInOfficeOrderDetailsRequest(
                buyerIDs = listOf(),
                fromDate = null,
                isSupplier = true,
                supplierIDs = listOf(),
                toDate = null,"1","50"
            )
        )) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> {
                goodsReturnList = response.value.data.orEmpty()
                prepareFilteredSecondaryList(id)
            }
        }
    }

    private val _goodsReturnListnew = MutableLiveData<List<GoodsReturnDataNew>>()
    val goodsReturnListnew: LiveData<List<GoodsReturnDataNew>> = _goodsReturnListnew

    fun loadDummyJson() {
        val jsonString = """
           {
    "data": [
        {
            "id": "b01c0b9a-52bb-449a-9663-56873e5af041",
            "saleBillNo": "25-26/DLGR 253",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": null,
            "billDate": "2025-06-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "b01c0b9a-52bb-449a-9663-56873e5af041",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 253",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "f030f0ba-94aa-404b-bf75-187bd5e83de8",
            "saleBillNo": "25-26/DLGR 256",
            "totalQty": 9.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": null,
            "billDate": "2025-06-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "f030f0ba-94aa-404b-bf75-187bd5e83de8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 256",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SHIRT",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "f030f0ba-94aa-404b-bf75-187bd5e83de8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 256",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SUIT",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "f030f0ba-94aa-404b-bf75-187bd5e83de8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 256",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "2f322b62-b05b-42db-8af4-f9feaf29efcd",
            "saleBillNo": "25-26/DLGR 255",
            "totalQty": 5.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": null,
            "billDate": "2025-06-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "2f322b62-b05b-42db-8af4-f9feaf29efcd",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 255",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "77b430f0-80b7-459d-a623-506c6e90fe4d",
            "saleBillNo": "25-26/DLGR 362",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": "PENDING",
            "billDate": "2025-06-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "77b430f0-80b7-459d-a623-506c6e90fe4d",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 362",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "7fa3e457-4b0e-48d1-85cb-0ba14aad585f",
            "saleBillNo": "25-26/DLGR 254",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": null,
            "billDate": "2025-06-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "7fa3e457-4b0e-48d1-85cb-0ba14aad585f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 254",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": null,
                    "noChangeQty": 1,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": "1 WRONG ENTRY",
                    "status": null
                }
            ]
        },
        {
            "id": "12f07ba9-921c-4acd-997e-87c6ed70a201",
            "saleBillNo": "25-26/DLGR 257",
            "totalQty": 5.00,
            "totalAmt": 0.00,
            "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
            "supplierName": "DL17874 TIWARI CREATION",
            "status": null,
            "billDate": "2025-06-17T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "12f07ba9-921c-4acd-997e-87c6ed70a201",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 257",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "PAGDI",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "12f07ba9-921c-4acd-997e-87c6ed70a201",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 257",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SHIRT",
                    "salePartyName": "DL22285 PARIDHAN SHOPPING MALL SAREES : AZAMGARH",
                    "salePartyEmail": null,
                    "salePartyMob": "9335662410",
                    "subPartyName": "PADMINI DRESSES : BHUBANESWAR",
                    "supplierName": "DL17874 TIWARI CREATION",
                    "supplierMob": "9310568963",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "bd163f4b-1cf8-4fd6-b173-87abcadd31b7",
            "saleBillNo": "25-26/DLGR 316",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-11T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "bd163f4b-1cf8-4fd6-b173-87abcadd31b7",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 316",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "cea84be1-5f26-499f-8582-6828e57a6058",
            "saleBillNo": "25-26/DLGR 325",
            "totalQty": 12.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-14T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "cea84be1-5f26-499f-8582-6828e57a6058",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 325",
                    "amount": 0.00,
                    "qty": 12,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR CONDITIONER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUBPARTY DUMMY",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "96c76297-4599-4e25-9274-06923a39a275",
            "saleBillNo": "25-26/DLGR 326",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-14T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "96c76297-4599-4e25-9274-06923a39a275",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 326",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "d0018710-dbc0-44b0-8cfd-bb00412e0785",
            "saleBillNo": "25-26/DLGR 327",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-14T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "d0018710-dbc0-44b0-8cfd-bb00412e0785",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 327",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY COMB SET",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "9de4903c-3d44-47fa-903b-8e4a4854f438",
            "saleBillNo": "25-26/DLGR 330",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "9de4903c-3d44-47fa-903b-8e4a4854f438",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 330",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BLAZER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "9de4903c-3d44-47fa-903b-8e4a4854f438",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 330",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY SCISSORS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "8ff6e653-5991-4d1c-a033-70094288ea39",
            "saleBillNo": "25-26/DLGR 329",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "8ff6e653-5991-4d1c-a033-70094288ea39",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 329",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POWDER PUFF",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "8ff6e653-5991-4d1c-a033-70094288ea39",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 329",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SOCKS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "8ff6e653-5991-4d1c-a033-70094288ea39",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 329",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SOCKS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "1b86d08f-f9be-454a-aa12-22a9fba3e84f",
            "saleBillNo": "25-26/DLGR 335",
            "totalQty": 21.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "1b86d08f-f9be-454a-aa12-22a9fba3e84f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 335",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "WALL FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "1b86d08f-f9be-454a-aa12-22a9fba3e84f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 335",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POTTY SEAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "1b86d08f-f9be-454a-aa12-22a9fba3e84f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 335",
                    "amount": 0.00,
                    "qty": 6,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BOYS COTI",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "1b86d08f-f9be-454a-aa12-22a9fba3e84f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 335",
                    "amount": 0.00,
                    "qty": 8,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "LED LIGHT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "f060da7e-0578-4958-9e1f-554f75a5bc5f",
            "saleBillNo": "25-26/DLGR 331",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "f060da7e-0578-4958-9e1f-554f75a5bc5f",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 331",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY ROMPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "1d6bec9f-ef5c-49aa-b29e-acbf1b1317db",
            "saleBillNo": "25-26/DLGR 336",
            "totalQty": 23.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "1d6bec9f-ef5c-49aa-b29e-acbf1b1317db",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 336",
                    "amount": 0.00,
                    "qty": 6,
                    "changedQty": null,
                    "noChangeQty": null,
                    "itemName": "CHOLI",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "1d6bec9f-ef5c-49aa-b29e-acbf1b1317db",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 336",
                    "amount": 0.00,
                    "qty": 6,
                    "changedQty": null,
                    "noChangeQty": null,
                    "itemName": "BABY DAIPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "1d6bec9f-ef5c-49aa-b29e-acbf1b1317db",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 336",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 5,
                    "noChangeQty": null,
                    "itemName": "KNITTED ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "1d6bec9f-ef5c-49aa-b29e-acbf1b1317db",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 336",
                    "amount": 0.00,
                    "qty": 6,
                    "changedQty": null,
                    "noChangeQty": null,
                    "itemName": "HANKY SET",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "4d6b2bed-efee-47fb-af1e-78abb3de8148",
            "saleBillNo": "25-26/DLGR 332",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "4d6b2bed-efee-47fb-af1e-78abb3de8148",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 332",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POTTY SEAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "94a70111-63f3-49a3-9da6-9fe10a6f5bd3",
            "saleBillNo": "25-26/DLGR 334",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "94a70111-63f3-49a3-9da6-9fe10a6f5bd3",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 334",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "WOODEN FRAMES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "57ebae4a-c621-4b37-bb65-145e25909994",
            "saleBillNo": "25-26/DLGR 333",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "57ebae4a-c621-4b37-bb65-145e25909994",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 333",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NIGHTY",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "2f00eaa3-0b14-4261-ac39-6c6704ad5953",
            "saleBillNo": "25-26/DLGR 328",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-15T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "2f00eaa3-0b14-4261-ac39-6c6704ad5953",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 328",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUB PARTY 2",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "e0a56313-dc27-4ad1-91b1-658b9296152c",
            "saleBillNo": "25-26/DLGR 338",
            "totalQty": 6.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "e0a56313-dc27-4ad1-91b1-658b9296152c",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 338",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "e0a56313-dc27-4ad1-91b1-658b9296152c",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 338",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY COMB SET",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "e0a56313-dc27-4ad1-91b1-658b9296152c",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 338",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "COAT PANT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "755f50c6-281a-4f11-a982-146bb3b2fb20",
            "saleBillNo": "25-26/DLGR 340",
            "totalQty": 10.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-16T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "755f50c6-281a-4f11-a982-146bb3b2fb20",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 340",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "LED",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "755f50c6-281a-4f11-a982-146bb3b2fb20",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 340",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "SOCKS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "755f50c6-281a-4f11-a982-146bb3b2fb20",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 340",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY SCISSORS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "755f50c6-281a-4f11-a982-146bb3b2fb20",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 340",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "bc05d1f7-eb51-4eb5-a2f0-7a0b2c891812",
            "saleBillNo": "25-26/DLGR 343",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-17T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "bc05d1f7-eb51-4eb5-a2f0-7a0b2c891812",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 343",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                },
                {
                    "id": "bc05d1f7-eb51-4eb5-a2f0-7a0b2c891812",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 343",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR CONDITIONER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "cf71b909-4182-464d-aac2-076b4193996e",
            "saleBillNo": "25-26/DLGR 342",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-17T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "cf71b909-4182-464d-aac2-076b4193996e",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 342",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY DAIPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "1f5c9081-4ad5-4afc-83d2-1fd309bc5f25",
            "saleBillNo": "25-26/DLGR 348",
            "totalQty": 2.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "1f5c9081-4ad5-4afc-83d2-1fd309bc5f25",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 348",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CUTTER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "14b0628e-8cb6-45a9-991c-f1c9ae8d0438",
            "saleBillNo": "25-26/DLGR 352",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL4 ANUPAM VIVIDH VASTU VIKRETA",
            "supplierName": "DL17028 KARNIKA GARMENTS PVT LTD",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "14b0628e-8cb6-45a9-991c-f1c9ae8d0438",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 352",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "JEANS",
                    "salePartyName": "DL4 ANUPAM VIVIDH VASTU VIKRETA",
                    "salePartyEmail": null,
                    "salePartyMob": "9415239080",
                    "subPartyName": null,
                    "supplierName": "DL17028 KARNIKA GARMENTS PVT LTD",
                    "supplierMob": "9810012558",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "28026d8f-f6a6-4fec-b2db-7f440f353ab1",
            "saleBillNo": "25-26/DLGR 355",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "28026d8f-f6a6-4fec-b2db-7f440f353ab1",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 355",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NIGHTY",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUBPARTY DUMMY",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "57095519-1bab-4275-a7a7-c058cd4babe2",
            "saleBillNo": "25-26/DLGR 354",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "57095519-1bab-4275-a7a7-c058cd4babe2",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 354",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "0db91756-f6fb-4e76-8c2c-c3c166a887c5",
            "saleBillNo": "25-26/DLGR 349",
            "totalQty": 2.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "0db91756-f6fb-4e76-8c2c-c3c166a887c5",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 349",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CUTTER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "e060f794-d3bc-4536-b52b-b65a1d59c55e",
            "saleBillNo": "25-26/DLGR 356",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "e060f794-d3bc-4536-b52b-b65a1d59c55e",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 356",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "VASKET",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUBPARTY DUMMY",
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "df96fc3f-0b5a-452e-9099-5737cd8190e7",
            "saleBillNo": "25-26/DLGR 351",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "df96fc3f-0b5a-452e-9099-5737cd8190e7",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 351",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POTTY SEAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "1f2aaca3-714a-433f-91c2-426fbcb6c32b",
            "saleBillNo": "25-26/DLGR 357",
            "totalQty": 5.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "1f2aaca3-714a-433f-91c2-426fbcb6c32b",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 357",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POTTY SEAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "0fcd53ed-1d00-4cc8-8b82-e6f47022fc6b",
            "saleBillNo": "25-26/DLGR 353",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "0fcd53ed-1d00-4cc8-8b82-e6f47022fc6b",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 353",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUBPARTY DUMMY",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "87c23b35-d88a-4531-8d2b-ac9e093eb25c",
            "saleBillNo": "25-26/DLGR 350",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "87c23b35-d88a-4531-8d2b-ac9e093eb25c",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 350",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY POTTY SEAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "660586c6-9885-46ab-b2bf-ae6cb30c40b9",
            "saleBillNo": "25-26/DLGR 347",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "660586c6-9885-46ab-b2bf-ae6cb30c40b9",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 347",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ACCESSORIES",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": "SUBPARTY DUMMY",
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "277722e2-4749-4c2b-bfa4-41416782f569",
            "saleBillNo": "25-26/DLGR 358",
            "totalQty": 8.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-18T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "277722e2-4749-4c2b-bfa4-41416782f569",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 358",
                    "amount": 0.00,
                    "qty": 8,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY ROMPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "902dc3bd-69bf-4f71-9846-dad2ea98c133",
            "saleBillNo": "25-26/DLGR 360",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": null,
            "billDate": "2025-07-21T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "902dc3bd-69bf-4f71-9846-dad2ea98c133",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 360",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CLIPPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "3e2f3d72-e61a-4b96-8a6c-658ec20bfbd2",
            "saleBillNo": "25-26/DLGR 359",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": null,
            "billDate": "2025-07-21T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "3e2f3d72-e61a-4b96-8a6c-658ec20bfbd2",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 359",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CLIPPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                },
                {
                    "id": "3e2f3d72-e61a-4b96-8a6c-658ec20bfbd2",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 359",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY DAIPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": null
                }
            ]
        },
        {
            "id": "0814f0a7-f9fe-4cbf-a192-e6ed563da10b",
            "saleBillNo": "25-26/DLGR 361",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-21T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "0814f0a7-f9fe-4cbf-a192-e6ed563da10b",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 361",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CLIPPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "d0a487b8-a2af-448a-9d03-60456bf7a7ae",
            "saleBillNo": "25-26/DLGR 363",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-21T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "d0a487b8-a2af-448a-9d03-60456bf7a7ae",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 363",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY NAIL CLIPPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "22bc7358-3c60-48f7-b15c-0d6b27b5de60",
            "saleBillNo": "25-26/DLGR 364",
            "totalQty": 1.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-21T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "22bc7358-3c60-48f7-b15c-0d6b27b5de60",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 364",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY ROMPER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "cece6772-ee75-4b7f-afa7-92d8878de615",
            "saleBillNo": "25-26/DLGR 378",
            "totalQty": 5.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "cece6772-ee75-4b7f-afa7-92d8878de615",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 378",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR CONDITIONER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "cece6772-ee75-4b7f-afa7-92d8878de615",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 378",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "2cdffc5d-b328-426d-9d23-6f49f4c15ef0",
            "saleBillNo": "25-26/DLGR 372",
            "totalQty": 4.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "2cdffc5d-b328-426d-9d23-6f49f4c15ef0",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 372",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ALUMINIUM EXTRUTED SECTION",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "2cdffc5d-b328-426d-9d23-6f49f4c15ef0",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 372",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY COAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "f9c3b22d-eaee-41d1-9506-58d3e4e46aa0",
            "saleBillNo": "25-26/DLGR 373",
            "totalQty": 12.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "f9c3b22d-eaee-41d1-9506-58d3e4e46aa0",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 373",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY COTY",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "f9c3b22d-eaee-41d1-9506-58d3e4e46aa0",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 373",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ALUMINIUM EXTRUTED SECTION",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "f9c3b22d-eaee-41d1-9506-58d3e4e46aa0",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 373",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIRPODS PRO",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "c70f3f68-e18e-4a4b-a81e-3438c1f8fed8",
            "saleBillNo": "25-26/DLGR 370",
            "totalQty": 11.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "c70f3f68-e18e-4a4b-a81e-3438c1f8fed8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 370",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "2U RACK",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "c70f3f68-e18e-4a4b-a81e-3438c1f8fed8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 370",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABYBIBS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "c70f3f68-e18e-4a4b-a81e-3438c1f8fed8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 370",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY GARMENTS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "c70f3f68-e18e-4a4b-a81e-3438c1f8fed8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 370",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY COAT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "da9828f7-0ef6-46e1-87d5-94bc2435b4cc",
            "saleBillNo": "25-26/DLGR 369",
            "totalQty": 9.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "da9828f7-0ef6-46e1-87d5-94bc2435b4cc",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 369",
                    "amount": 0.00,
                    "qty": 4,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BLANKET",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "da9828f7-0ef6-46e1-87d5-94bc2435b4cc",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 369",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ALUMNIUM FOIL",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "da9828f7-0ef6-46e1-87d5-94bc2435b4cc",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 369",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BED COVER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "451b954b-e011-4851-a8ba-27f4aaec27e9",
            "saleBillNo": "25-26/DLGR 374",
            "totalQty": 6.00,
            "totalAmt": 0.00,
            "salePartyName": "DL4 ANUPAM VIVIDH VASTU VIKRETA",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "451b954b-e011-4851-a8ba-27f4aaec27e9",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 374",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR CONDITIONER",
                    "salePartyName": "DL4 ANUPAM VIVIDH VASTU VIKRETA",
                    "salePartyEmail": null,
                    "salePartyMob": "9415239080",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "451b954b-e011-4851-a8ba-27f4aaec27e9",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 374",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ANDI 50071000",
                    "salePartyName": "DL4 ANUPAM VIVIDH VASTU VIKRETA",
                    "salePartyEmail": null,
                    "salePartyMob": "9415239080",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "0e15f264-baf1-4817-ade4-cf8636b9c368",
            "saleBillNo": "25-26/DLGR 371",
            "totalQty": 6.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "0e15f264-baf1-4817-ade4-cf8636b9c368",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 371",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIRPODS PRO",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "0e15f264-baf1-4817-ade4-cf8636b9c368",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 371",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABIES SUIT",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "b092ceec-2686-4f95-b685-3fcfd7e90d79",
            "saleBillNo": "25-26/DLGR 366",
            "totalQty": 3.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "b092ceec-2686-4f95-b685-3fcfd7e90d79",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 366",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIRPODS PRO",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "afd128a9-56ab-49cd-928f-885980bb01a8",
            "saleBillNo": "25-26/DLGR 375",
            "totalQty": 7.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "afd128a9-56ab-49cd-928f-885980bb01a8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 375",
                    "amount": 0.00,
                    "qty": 5,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ALUMNIUM FOIL",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "afd128a9-56ab-49cd-928f-885980bb01a8",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 375",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR CONDITIONER",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "b0a4dca5-c5fc-4185-b04f-d131ba58d56e",
            "saleBillNo": "25-26/DLGR 368",
            "totalQty": 8.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "b0a4dca5-c5fc-4185-b04f-d131ba58d56e",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 368",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "2U RACK",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "b0a4dca5-c5fc-4185-b04f-d131ba58d56e",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 368",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ATTENDENCE MACHINE 85437099",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "b0a4dca5-c5fc-4185-b04f-d131ba58d56e",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 368",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "BABY SCISSORS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "3dc7dbfd-9dab-4f64-91b1-e7c2de0c8fe2",
            "saleBillNo": "25-26/DLGR 376",
            "totalQty": 2.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL3827 DHARMENDER TEXTILE",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "3dc7dbfd-9dab-4f64-91b1-e7c2de0c8fe2",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 376",
                    "amount": 0.00,
                    "qty": 2,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "ALUMNIUM FOIL",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL3827 DHARMENDER TEXTILE",
                    "supplierMob": "7290042960",
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        },
        {
            "id": "12ccaa3d-24f8-492e-bee1-8ba8138561d6",
            "saleBillNo": "25-26/DLGR 365",
            "totalQty": 4.00,
            "totalAmt": 0.00,
            "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
            "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
            "status": "PENDING",
            "billDate": "2025-07-22T00:00:00",
            "goodsReturnDetailList": [
                {
                    "id": "12ccaa3d-24f8-492e-bee1-8ba8138561d6",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 365",
                    "amount": 0.00,
                    "qty": 1,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AFGANI DRESS",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                },
                {
                    "id": "12ccaa3d-24f8-492e-bee1-8ba8138561d6",
                    "grSecId": null,
                    "saleBillNo": "25-26/DLGR 365",
                    "amount": 0.00,
                    "qty": 3,
                    "changedQty": 0,
                    "noChangeQty": 0,
                    "itemName": "AIR FAN",
                    "salePartyName": "DL3331 WRONG TEST SYBER PARTY A/C",
                    "salePartyEmail": "SADHANA@SSSSYBERTECH.COM",
                    "salePartyMob": "7986511273",
                    "subPartyName": null,
                    "supplierName": "DL23435 DHARMENDER TEXTILE TEST",
                    "supplierMob": null,
                    "remark": null,
                    "status": "PENDING"
                }
            ]
        }
    ],
    "message": "Success",
    "success": true,
    "error": false,
    "responsecode": "200"
}
            """.trimIndent()
        val response = Gson().fromJson(jsonString, GoodsReturnResponse::class.java)

        _goodsReturnListnew.value = response.data ?: emptyList()
        originalList=response.data ?: emptyList()
        prepareFilteredList()
    }




    fun prepareFilteredList() = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0
        goodsReturnList.forEach {
            val orderItemList =
                if (searchValue.isBlank()) it.goodsReturnPrimaryData
                else it.goodsReturnPrimaryData?.filter {
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

    fun prepareFilteredSecondaryList(id: String) = viewModelScope.launch(Dispatchers.Default) {
        val list = mutableListOf<BaseWidget>()
        var count = 0

        goodsReturnList.forEach { data ->
            if (data.id == id) {  // ✅ Match only specific ID
                val orderItemList = if (searchValue.isBlank()) {
                    data.goodsReturnDetailList
                } else {
                    data.goodsReturnDetailList?.filter {
                        it.saleBillNo?.contains(searchValue, true) == true ||
                                it.salePartyName?.contains(searchValue, true) == true ||
                                it.supplierName?.contains(searchValue, true) == true
                    }
                }

                if (!orderItemList.isNullOrEmpty()) {
                    list.add(TitleSubtitleWrapper(id = data.billDate!!, title = data.billDate))
                    list.addAll(orderItemList)
                    count += orderItemList.size
                }
            }
        }

        clearWidgetList()
        addItemToWidgetList(list)

        withContext(Dispatchers.Main) {
            listDataChanged()
            hideProgressBar()
        }
    }


    fun prepareFilteredListGr() = viewModelScope.launch(Dispatchers.Default) {
        val filteredList = originalList.filter { item ->
            searchValue.isBlank() || item.saleBillNo?.contains(searchValue, ignoreCase = true) == true
                    || item.salePartyName?.contains(searchValue, ignoreCase = true) == true
                    || item.billDate?.contains(searchValue, ignoreCase = true) == true
                    || item.supplierName?.contains(searchValue, ignoreCase = true) == true
                    || item.supplierName?.contains(searchValue, ignoreCase = true) == true
        }
        _goodsReturnListnew.postValue(filteredList)
    }




}