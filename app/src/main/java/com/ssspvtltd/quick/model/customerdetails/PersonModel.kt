package com.ssspvtltd.quick.model.customerdetails

import android.graphics.Bitmap

data class PersonModel(
    var personName: String = "",
    var frontImage: String = "",
    var backImage: String = "",
    var aadharFrontBitmap: Bitmap? = null,
    var aadharBackBitmap: Bitmap? = null,
    var aadharFrontBase64: String? = null,
    var aadharBackBase64: String? = null
)
