package com.ssspvtltd.quick.model.mailbox

import android.os.Parcelable
import com.ssspvtltd.quick.base.recycler.data.BaseViewType
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import kotlinx.android.parcel.Parcelize

data class MailResponse(
    val data: List<MailData>,
    val message: String,
    val success: Boolean,
    val error: Boolean,
    val responsecode: String
)

data class MailData(
    val billDate: String,
    val mailDetailList: List<MailDetail>
)

@Parcelize
data class MailDetail(
    val orderDate: String,
    val mailDate: String,
    val orderNo: String,
    val purchaseBillNo: String,
    val saleParty: String,
    val purchaseParty: String,
    val netAmt: Double,
    val remark: String,
    val filePath: String
) : BaseWidget, Parcelable {


    // View type for Recycler
    override val viewType: BaseViewType
        get() = CommonViewType.DATA

    // Unique ID for diffing in adapters
    override fun getUniqueId(): String = orderNo + mailDate


}




