package com.ssspvtltd.quick.ui.tour.model

data class LeadRequest(

    val id: String? = null,

    val leadNo: Int? = 0,

    val date: String = "",

    val leadTypeId: String? = null,

    val marketerId: String? = null,

    val marketerName: String = "",

    val leadTypeName: String? = "",

    val ownerName: String = "",

    val firmName: String = "",

    val mobileNo: String = "",

    val whatsappNo: String = "",

    val stateId: String? = null,

    val stateName: String = "",

    val stationId: String? = null,

    val stationName: String = "",
    val station_Name: String = "",

    val stationSubDistrictName: String = "",

    val appInstallStatus: Boolean = false,

    val insertStatus: Boolean = true,

    val updateStatus: Boolean = false,

    val deletedStatus: Boolean = false,

    val createdBy: String? = null,

    val createdByName: String = "",

    val updatedBy: String? = null,

    val updatedByName: String = "",

    val status: String = "",

    val updatedDate: String? = null,

    val companyId: String? = null,

    val categoryId: String? = null,

    val categoryName: String = "",

    val address: String = "",

    val pinCode: String = "",

    val accountID: String? = null,

    val gradeID: String? = null,

    val gradeName: String = "",

    val workingBranch: String = "",

    val oldAgentName: String = "",

    val shopCategory: List<ShopCategoryRequest> = emptyList(),

    val yearlySale: String = "",

    val shopArea: String = "",
    val workingBranchs: String = "",

    val remark: String = "",

    val latitude: String = "",

    val longitude: String = "",

    val selfieImage1: String = "",

    val selfieImage2: String = "",

    val shopImage1: String = "",

    val shopImage2: String = "",

    val shopImage3: String = "",

    val shopImage4: String = "",
    val shopImage5: String = "",

    val selfieImageURL1: String = "",
    val selfieImageURL2: String = "",

    val shopImageURL1: String = "",

    val shopImageURL2: String = "",

    val shopImageURL3: String = "",

    val shopImageURL4: String = "",

    val shopImageURL5: String = "",
    val shopImage6: String = ""


)

data class ShopCategoryRequest(

    val id: String = "",
    val name: String = ""
)