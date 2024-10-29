package com.ssspvtltd.quick_app.model.checkincheckout

data class CheckInRequest(
    var address: String,
    var customerList: List<CustomerData>,
    var lattitude: String,
    var longitude: String,
    var remarks: String
)