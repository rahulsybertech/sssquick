package com.ssspvtltd.quick.model.order.add.salepartydetails

data class Data(
    val avgDays: Int,
    var avlLimit: String,
    val defSubPartyId: Any?,
    val defSubPartyName: String,
    val defTransport: List<DefTransport>?,
    val emailId: String,
    val mobileNo: String,
    val subPartyList: List<com.ssspvtltd.quick.model.order.add.salepartyNewList.SubParty>
  /*  val subPartyList: List<SubParty>?*/
)