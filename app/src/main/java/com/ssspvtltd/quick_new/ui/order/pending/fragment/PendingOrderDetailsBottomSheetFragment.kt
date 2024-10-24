package com.ssspvtltd.quick_new.ui.order.pending.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.BaseBottomDialog
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateBD
import com.ssspvtltd.quick_new.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick_new.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick_new.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick_new.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick_new.ui.order.pending.adapter.PendingOrderImageListAdapter
import com.ssspvtltd.quick_new.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick_new.ui.order.pending.adapter.PendingOrderPDFListAdapter
import com.ssspvtltd.quick_new.utils.extension.getParcelableExt
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingOrderDetailsBottomSheetFragment :
    BaseBottomDialog<FragmentPendingOrderDetailsBottomSheetBinding, BaseViewModel>() {
    private var pendingOrderItem: PendingOrderItem? = null
    private lateinit var mAdapter: PendingOrderItemAdapter
    private lateinit var imgAdapter: PendingOrderImageListAdapter
    private lateinit var pdfAdapter: PendingOrderPDFListAdapter
    override val inflate: InflateBD<FragmentPendingOrderDetailsBottomSheetBinding>
        get() = FragmentPendingOrderDetailsBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingOrderItem    = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
        mAdapter            = PendingOrderItemAdapter(pendingOrderItem?.orderNo, null)
        imgAdapter          = PendingOrderImageListAdapter(pendingOrderItem?.imagePathList, ::imageCallBack)
        pdfAdapter          = PendingOrderPDFListAdapter(pendingOrderItem?.pdfPathList, ::showPdfPreviewDialog)

        mAdapter.submitList(pendingOrderItem?.itemDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.setCanceledOnTouchOutside(false)
        binding.recyclerView.adapter    = mAdapter
        setRecyclerViewAdapters()

        initViews()
        registerListeners()
    }

    private fun setRecyclerViewAdapters() {
        binding.rvImg.layoutManager     =  LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImg.adapter           = imgAdapter

        binding.rvPdfDoc.layoutManager  =  LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPdfDoc.adapter        = pdfAdapter
    }

    private fun initViews() = with(binding) {
        tvSaleParty.text    = pendingOrderItem?.salePartyName ?: "N/A"
        tvSupplier.text     = pendingOrderItem?.supplierName ?: "N/A"
        tvSubParty.text     = pendingOrderItem?.subPartyName ?: "Self"
        tvStatus.text       = pendingOrderItem?.status ?: "--"
        tvRemark.text       = pendingOrderItem?.remark ?: "--"
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

    private fun imageCallBack(url: String) {
        showImagePreviewDialog(requireContext(), url)
    }

    private fun showImagePreviewDialog(context: Context, imageUrl: String) {

        val imageView = ImageView(context)

        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        val builder = AlertDialog.Builder(context)
        builder.setView(imageView)
        builder.setCancelable(true)
        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showPdfPreviewDialog(url: String) {
        dismiss()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val chooser = Intent.createChooser(intent, "Open PDF")
        try {
            requireActivity().startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT).show()
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