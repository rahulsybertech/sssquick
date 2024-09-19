package com.ssspvtltd.quick.ui.order.pending.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingOrderDetailsBottomSheetFragment :
    BaseBottomDialog<FragmentPendingOrderDetailsBottomSheetBinding, BaseViewModel>() {
    private var pendingOrderItem: PendingOrderItem? = null
    private lateinit var mAdapter: PendingOrderItemAdapter
    override val inflate: InflateBD<FragmentPendingOrderDetailsBottomSheetBinding>
        get() = FragmentPendingOrderDetailsBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingOrderItem = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
        mAdapter = PendingOrderItemAdapter(pendingOrderItem?.orderNo, null)
        mAdapter.submitList(pendingOrderItem?.itemNameList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.setCanceledOnTouchOutside(false)
        binding.recyclerView.adapter = mAdapter
        initViews()
        registerListeners()
    }

    private fun initViews() = with(binding) {
        tvSaleParty.text = pendingOrderItem?.salePartyName ?: "N/A"
        tvSupplier.text = pendingOrderItem?.supplierName ?: "N/A"
        tvSubParty.text = pendingOrderItem?.subPartyName ?: "Self"
        tvStatus.text = "N/A"
        tvRemark.text = "N/A"
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PENDING_ORDER_ID, pendingOrderItem?.orderID)
            }
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.pendingorderFragment, true)
                .build()
            findNavController().navigate(R.id.addOrderFragment, bundle, navOptions)
            dismissAllowingStateLoss()
        }
    }

    companion object {
        fun newInstance(pendingOrderItem: PendingOrderItem): PendingOrderDetailsBottomSheetFragment {
            return PendingOrderDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, pendingOrderItem)
                }
            }
        }
    }
}