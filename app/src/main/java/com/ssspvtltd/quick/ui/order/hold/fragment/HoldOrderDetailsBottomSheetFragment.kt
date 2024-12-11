package com.ssspvtltd.quick.ui.order.hold.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentHoldOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.order.hold.HoldOrderItem
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateRequest
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderImageListAdapter
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderItemAdapter
import com.ssspvtltd.quick.ui.order.hold.holdinterface.VisibilityListener
import com.ssspvtltd.quick.utils.DownloadCompleteReceiver
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class HoldOrderDetailsBottomSheetFragment(private var holdOrderItem: HoldOrderItem) :
    BaseBottomDialog<FragmentHoldOrderDetailsBottomSheetBinding, BaseViewModel>() {
    private lateinit var mAdapter: HoldOrderItemAdapter
    private lateinit var imgAdapter: HoldOrderImageListAdapter
    override val inflate: InflateBD<FragmentHoldOrderDetailsBottomSheetBinding>
        get() = FragmentHoldOrderDetailsBottomSheetBinding::inflate
    private var pdfUrl = ""
    override fun initViewModel(): BaseViewModel = getViewModel()

    var visibilityListener: VisibilityListener? = null

    override fun onStart() {
        super.onStart()
        visibilityListener?.onVisibilityChanged(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = HoldOrderItemAdapter(holdOrderItem.orderNo, null)
        imgAdapter = HoldOrderImageListAdapter(holdOrderItem.imagePathList, ::imageCallBack)
        mAdapter.submitList(holdOrderItem.itemDetail)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        binding.recyclerView.adapter = mAdapter
        setRecyclerViewAdapters()
        registerListeners()
    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        if (holdOrderItem.isAdjustedStatus == false) {
            btnEdit.visibility = View.VISIBLE
        } else {
            btnEdit.visibility = View.GONE
        }
        tvSaleNo.text = holdOrderItem.orderNo ?: ""
        lifecycleScope.launch {
            tvManufactureData.text = viewModel.prefHelper.getUserName() ?: ""
        }
        btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PENDING_ORDER_ID, holdOrderItem.orderID)
            }
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.holdorderFragment, true)
                .build()
            findNavController().navigate(R.id.addOrderFragment, bundle, navOptions)
            dismissAllowingStateLoss()
        }
        tvSaleParty.text = holdOrderItem.salePartyName
        tvSupplier.text = holdOrderItem.supplierName

        if (holdOrderItem.subPartyasRemark.isNotNullOrBlank()) {
            lvlSubParty.text = "Remark     : "
            tvSubParty.text = holdOrderItem.subPartyasRemark
        } else {
            lvlSubParty.text = "Sub Party  : "
            tvSubParty.text = holdOrderItem.subPartyName
        }

        tvStatus.text = holdOrderItem.status ?: "--"
        tvRemark.text = holdOrderItem.remark ?: "--"

        binding.regeneratedPdf.setOnClickListener {
            val pendingOrderPDFRegenerateRequest =
                PendingOrderPDFRegenerateRequest.PendingOrderPDFRegenerateRequestItem(
                    holdOrderItem.orderID
                )
            viewModel.getPDF(pendingOrderPDFRegenerateRequest)
        }

        viewModel.fetchPdfUrl.observe(viewLifecycleOwner, Observer { pdfUrl ->

            println("GETTING_PDF_URL - $pdfUrl")
            downloadPdfToDownloads(requireContext(), pdfUrl)

        })

        deleteItem.setOnClickListener {
            // abhinav
            confirmDelete(holdOrderItem.orderID!!)
        }

        viewModel.fetchHoldDeleteMessage.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

    }

    private fun confirmDelete(orderId : String){
        SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE).apply {
            titleText = "Delete Order"
            contentText = "Are you sure to Delete?"
            setCancelable(false)
            confirmText = "Yes"
            cancelText = "No"
            confirmButtonBackgroundColor = getColor(requireContext(), R.color.red_2)
            setCancelClickListener {
                dismiss()
            }
            setConfirmClickListener {
                println("GETTING_ORDER_ID $orderId")
                viewModel.deleteOrder(orderId)
                visibilityListener?.onVisibilityChanged(false)
                dismiss()
            }
        }.show()

        // val builder = AlertDialog.Builder(requireActivity())
        // builder.setMessage("Do you want to Delete Order?")
        //     .setCancelable(false)
        //     .setPositiveButton("Yes") { _, _ ->
        //         // Delete selected note from database
        //
        //     }
        //     .setNegativeButton("No") { _, _ ->
        //         // Dismiss the dialog
        //         dialog?.dismiss()
        //     }
        // val alert = builder.create()
        // alert.show()
        // alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red_2))

    }

    private fun imageCallBack(url: String) {
        if (url.contains(".pdf")) {
            showPdfPreviewDialog(url)
        } else {
            showImagePreviewDialog(requireContext(), url)
        }

    }

    private fun setRecyclerViewAdapters() {
        binding.rvImg.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImg.adapter = imgAdapter

    }

    private fun showImagePreviewDialog(context: Context, imageUrl: String) {

        val imageView = ImageView(context)

        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        val builder = AlertDialog.Builder(context)
        builder.setView(imageView)
        builder.setCancelable(false)
        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("TaG", "Image loaded successfully")
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    dialog.dismiss()
                    Toast.makeText(context, "Image load failed", Toast.LENGTH_SHORT).show()
                    return false
                }
            })
            .into(imageView)

    }

    @SuppressLint("MissingInflatedId")
    private fun showPdfPreviewDialog(url: String) {
        // dismiss()
        pdfUrl = url
        checkAndRequestPermissions()
        // showPdf(url)
        /*val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val chooser = Intent.createChooser(intent, "Open PDF")
        try {
            requireActivity().startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }*/

    }

    private fun showPdf(url: String) {
        val urlData = url.split("/")
        val fileName = urlData.lastOrNull() ?: "downloaded_file.pdf"  // Extract file name from URL
        Log.d("TaG", "showPdf: $fileName")

        // Use the public downloads directory
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            openPdf(file)
        } else {
            Log.i("TaG", "-----> Download start <--------")
            downloadPdf(url, fileName)
        }
    }

    private fun downloadPdf(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading PDF")
            setDescription("Please wait while the PDF is being downloaded")

            // Use the Downloads directory
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }

        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Create an instance of the custom BroadcastReceiver
        val receiver = DownloadCompleteReceiver(
            downloadId = downloadId,
            fileName = fileName,
            fragmentReference = WeakReference(requireParentFragment()), // Pass a weak reference to the fragment
            downloadManager = downloadManager,
            openPdf = { file -> openPdf(file) }
        )


        // Register the BroadcastReceiver
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireContext().registerReceiver(receiver, filter)
        }
    }

    // Function to open the PDF file
    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")

            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val chooser = Intent.createChooser(intent, "Open PDF")
        try {
            requireActivity().startActivity(chooser)
            if (isAdded && !requireActivity().isFinishing) {
                dismiss()
            }

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // showPdf(pdfUrl)
                downloadPdfToDownloads(requireContext(), pdfUrl)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            downloadPdfToDownloads(requireContext(), pdfUrl)
            // showPdf(pdfUrl)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // showPdf(pdfUrl)
                    downloadPdfToDownloads(requireContext(), pdfUrl)
                } else {
                }
            }

        }
    }

    fun downloadPdfToDownloads(context: Context, pdfUrl: String) {
        // Step 1: Get the Downloads directory
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = pdfUrl.substringAfterLast("/")
        val pdfFile = File(downloadsDir, fileName)

        // Step 2: Download the file
        try {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
            val url = URL(pdfUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(pdfFile)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            // Toast.makeText(context, "PDF downloaded to ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 3: Open the PDF
        openPdf(pdfFile)
        // try {
        //     val uri: Uri = FileProvider.getUriForFile(
        //         context,
        //         "com.ssspvtltd.quick.fileprovider",
        //         pdfFile
        //     )
        //
        //     val intent = Intent(Intent.ACTION_VIEW)
        //     intent.setDataAndType(uri, "application/pdf")
        //     intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //
        //     context.startActivity(intent)
        // } catch (e: Exception) {
        //     e.printStackTrace()
        //     Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        // }
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001


    }
}