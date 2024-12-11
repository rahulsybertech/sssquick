package com.ssspvtltd.quick.model.order.pending


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PendingOrderPDFRegenerateRequest : ArrayList<PendingOrderPDFRegenerateRequest.PendingOrderPDFRegenerateRequestItem>(){
    data class PendingOrderPDFRegenerateRequestItem(
        @SerializedName("recordId")
        @Expose
        val recordId: String?
    )
}