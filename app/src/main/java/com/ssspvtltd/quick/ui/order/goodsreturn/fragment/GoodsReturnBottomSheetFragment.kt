package com.ssspvtltd.quick.ui.order.goodsreturn.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentGoodReturnBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentHoldOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnItem
import com.ssspvtltd.quick.model.order.hold.HoldOrderItem
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderItemAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoodsReturnBottomSheetFragment :
    BaseBottomDialog<FragmentGoodReturnBottomSheetBinding, BaseViewModel>() {
    private var goodsReturnItem: GoodsReturnItem? = null
    override val inflate: InflateBD<FragmentGoodReturnBottomSheetBinding>
        get() = FragmentGoodReturnBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goodsReturnItem = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        registerListeners()
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        tvSaleParty.text=goodsReturnItem?.salePartyName ?: "N/A"
        tvSupplier.text=goodsReturnItem?.supplierName ?: "N/A"
        tvItem.text=goodsReturnItem?.itemName ?: "N/A"
        tvStatus.text=goodsReturnItem?.status ?: "N/A"
        tvSaleBillNo.text=goodsReturnItem?.saleBillNo ?: "N/A"
        tvAmount.text=goodsReturnItem?.amount.toString()
        tvPcs.text=goodsReturnItem?.qty.toString()
        tvPcsChange.text=goodsReturnItem?.changedQty.toString()
        tvPcsNoChange.text=goodsReturnItem?.noChangeQty.toString()
        tvRemark.text=goodsReturnItem?.remark ?: "N/A"
        // tvSaleBillDate.text=goodsReturnItem?. // pass through apapter constructor

    }

    companion object {
        fun newInstance(goodsReturnItem: GoodsReturnItem): GoodsReturnBottomSheetFragment {
            return GoodsReturnBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, goodsReturnItem)
                }
            }
        }
    }
}