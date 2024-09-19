package com.ssspvtltd.quick.ui.order.pendinglr.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentGoodReturnBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentHoldOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentPendingLrBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnItem
import com.ssspvtltd.quick.model.order.hold.HoldOrderItem
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrItem
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderItemAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingLrBottomSheetFragment :
    BaseBottomDialog<FragmentPendingLrBottomSheetBinding, BaseViewModel>() {
    private var pendingLrItem: PendingLrItem? = null
    override val inflate: InflateBD<FragmentPendingLrBottomSheetBinding>
        get() = FragmentPendingLrBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingLrItem = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        registerListeners()
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        tvSaleParty.text=pendingLrItem?.salePartyName ?: "N/A"
        tvSupplier.text=pendingLrItem?.supplierName ?: "N/A"
        tvQuantity.text=pendingLrItem?.qty.toString()
         // tvItem.text=pendingLrItem?.itemName ?: "N/A"
         // tvStatus.text=pendingLrItem?.status ?: "N/A"
         tvSaleBillNo.text=pendingLrItem?.billNo ?: "N/A"
         tvAmount.text=pendingLrItem?.amount.toString()
        // tvPcs.text=pendingLrItem?.qty.toString()
        //  tvRemark.text=pendingLrItem?.remark ?: "N/A"A
        //  tvSaleBillDate.text=pendingLrItem? // pass through apapter constructor

    }

    companion object {
        fun newInstance(pendingLrItem: PendingLrItem): PendingLrBottomSheetFragment {
            return PendingLrBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, pendingLrItem)
                }
            }
        }
    }
}