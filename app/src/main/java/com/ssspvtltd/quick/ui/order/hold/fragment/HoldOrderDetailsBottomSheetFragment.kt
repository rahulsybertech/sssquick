package com.ssspvtltd.quick.ui.order.hold.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentHoldOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.hold.HoldOrderItem
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderItemAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HoldOrderDetailsBottomSheetFragment :
    BaseBottomDialog<FragmentHoldOrderDetailsBottomSheetBinding, BaseViewModel>() {
    private var holdOrderItem: HoldOrderItem? = null
    private lateinit var mAdapter: HoldOrderItemAdapter
    override val inflate: InflateBD<FragmentHoldOrderDetailsBottomSheetBinding>
        get() = FragmentHoldOrderDetailsBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        holdOrderItem = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
        mAdapter = HoldOrderItemAdapter(holdOrderItem?.orderNo, null)
        mAdapter.submitList(holdOrderItem?.itemNameList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        binding.recyclerView.adapter = mAdapter
        registerListeners()
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        tvSaleParty.text=holdOrderItem?.salePartyName
        tvSupplier.text=holdOrderItem?.supplierName
        tvSubParty.text=holdOrderItem?.subPartyName
    }

    companion object {
        fun newInstance(holdOrderItem: HoldOrderItem): HoldOrderDetailsBottomSheetFragment {
            return HoldOrderDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, holdOrderItem)
                }
            }
        }
    }
}