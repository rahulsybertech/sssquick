package com.ssspvtltd.quick.model.customerdetails

import android.graphics.Bitmap

data class PersonModel(
    var id: String = "",
    var personName: String = "",
    var frontURL: String = "",
    var backURL: String = "",
    var aadharFrontBitmap: Bitmap? = null,
    var aadharBackBitmap: Bitmap? = null,
    var aadharFrontBase64: String? = null,
    var aadharBackBase64: String? = null
)

