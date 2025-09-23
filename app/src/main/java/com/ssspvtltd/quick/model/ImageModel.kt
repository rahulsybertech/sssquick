package com.ssspvtltd.quick.model

import android.os.Parcel
import android.os.Parcelable

data class ImageModel(
    val filePath: String
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(filePath)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ImageModel> {
        override fun createFromParcel(parcel: Parcel): ImageModel = ImageModel(parcel)
        override fun newArray(size: Int): Array<ImageModel?> = arrayOfNulls(size)
    }
}
