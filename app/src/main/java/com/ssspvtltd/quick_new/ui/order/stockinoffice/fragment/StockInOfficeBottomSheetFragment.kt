package com.ssspvtltd.quick_new.ui.order.stockinoffice.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.BaseBottomDialog
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateBD
import com.ssspvtltd.quick_new.databinding.FragmentStockInOfficeBottomSheetBinding
import com.ssspvtltd.quick_new.model.ARG_STOCK_IN_OFFICE_ORDER_ITEM
import com.ssspvtltd.quick_new.model.order.stockinoffice.StockInOfficeOrderItem
import com.ssspvtltd.quick_new.utils.extension.getParcelableExt
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import com.ssspvtltd.quick_new.utils.extension.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockInOfficeBottomSheetFragment :
    BaseBottomDialog<FragmentStockInOfficeBottomSheetBinding, BaseViewModel>() {
    private var stockInOfficeItem: StockInOfficeOrderItem? = null
    override val inflate: InflateBD<FragmentStockInOfficeBottomSheetBinding>
        get() = FragmentStockInOfficeBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockInOfficeItem = arguments?.getParcelableExt(ARG_STOCK_IN_OFFICE_ORDER_ITEM)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        registerListeners()
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        tvSaleParty.text=stockInOfficeItem?.salePartyName ?: "N/A"
        tvSupplier.text=stockInOfficeItem?.supplierName ?: "N/A"
        tvQuantity.text=stockInOfficeItem?.qty.toString()
         // tvItem.text=pendingLrItem?.itemName ?: "N/A"
         // tvStatus.text=pendingLrItem?.status ?: "N/A"
         tvPurchaseNo.text=stockInOfficeItem?.purchaseNo ?: "N/A"
         tvAmount.text=stockInOfficeItem?.amount.toString()
        // tvPcs.text=pendingLrItem?.qty.toString()
        //  tvRemark.text=pendingLrItem?.remark ?: "N/A"A
        //  tvSaleBillDate.text=pendingLrItem? // pass through apapter constructor

        if (stockInOfficeItem?.imagePaths.isNotNullOrBlank()) {
            if ((stockInOfficeItem?.imagePaths ?: "").contains(".pdf")) {
                ivDoc.setImageResource(R.drawable.ic_pdf)
            } else {
                ivDoc.setImageResource(R.drawable.ic_image)
            }
        }

        llDoc.setOnClickListener {
            if ((stockInOfficeItem?.imagePaths ?: "").contains(".pdf")) {
                showPdfPreviewDialog(stockInOfficeItem?.imagePaths ?: "")
            } else {
                showImagePreviewDialog(requireContext(), stockInOfficeItem?.imagePaths ?: "")
            }
        }

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
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(url), "application/pdf")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION) // Open in a new task

                startActivity(intent)
       /* val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pdf_preview, null)

        val webView: WebView = dialogView.findViewById(R.id.webView)
        val progressBar: ProgressBar = dialogView.findViewById(R.id.progressBar)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }

        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$url"
        webView.loadUrl(googleDocsUrl)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        dialog.show()*/
    }

    companion object {
        fun newInstance(stockInOfficeItem: StockInOfficeOrderItem): StockInOfficeBottomSheetFragment {
            return StockInOfficeBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STOCK_IN_OFFICE_ORDER_ITEM, stockInOfficeItem)
                }
            }
        }
    }
}