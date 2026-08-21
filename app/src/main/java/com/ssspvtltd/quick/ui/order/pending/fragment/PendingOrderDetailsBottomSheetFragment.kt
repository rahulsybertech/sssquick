package com.ssspvtltd.quick.ui.order.pending.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentPendingOrderDetailsBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.model.order.pending.PendingOrderPDFRegenerateRequest
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderImageListAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderItemAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderPDFListAdapter
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


@AndroidEntryPoint
class PendingOrderDetailsBottomSheetFragment :
    BaseBottomDialog<FragmentPendingOrderDetailsBottomSheetBinding, BaseViewModel>() {
    private var pendingOrderItem: PendingOrderItem? = null
    private lateinit var mAdapter: PendingOrderItemAdapter
    private lateinit var imgAdapter: PendingOrderImageListAdapter
    private lateinit var pdfAdapter: PendingOrderPDFListAdapter
    override val inflate: InflateBD<FragmentPendingOrderDetailsBottomSheetBinding>
        get() = FragmentPendingOrderDetailsBottomSheetBinding::inflate

    private var pdfUrl = ""
    override fun initViewModel(): BaseViewModel = getViewModel()

    private lateinit var downloadManager: DownloadManager
    var downloadId: Long? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingOrderItem = arguments?.getParcelableExt(ARG_PENDING_ORDER_ITEM)
        mAdapter = PendingOrderItemAdapter(pendingOrderItem?.orderNo, null)
        imgAdapter = PendingOrderImageListAdapter(pendingOrderItem?.imagePathList, ::imageCallBack)
        pdfAdapter = PendingOrderPDFListAdapter(
            pendingOrderItem?.pdfPathList, ::showPdfPreviewDialog
        )

        if (pendingOrderItem?.pdfPathList != null) {
            pdfUrl = pendingOrderItem?.pdfPathList?.get(0)?.pdfUrl.toString()
            println("GET_MY_PDF 2 $pdfUrl")
        }
        mAdapter.submitList(pendingOrderItem?.itemDetail)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.setCanceledOnTouchOutside(false)
        binding.recyclerView.adapter = mAdapter
        setRecyclerViewAdapters()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
                downloadPdfToDownloads(requireContext(), pdfUrl, false)
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        initViews()
        registerListeners()
    }

    private fun setRecyclerViewAdapters() {

        binding.rvImg.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImg.adapter = imgAdapter

        binding.rvPdfDoc.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPdfDoc.adapter = pdfAdapter
    }

    private fun initViews() = with(binding) {
        tvSaleParty.text = pendingOrderItem?.salePartyName ?: "N/A"
        tvSupplier.text = pendingOrderItem?.supplierName ?: "N/A"
        tvStatus.text = pendingOrderItem?.status ?: "--"
        tvRemark.text = pendingOrderItem?.remark ?: "--"
        tvSaleNo.text = pendingOrderItem?.orderNo ?: ""
        val schemeName = pendingOrderItem?.schemeName

        if (schemeName.isNullOrBlank()) {

            tvScheme.visibility = View.GONE
            scheme.visibility = View.GONE

        } else {

            tvScheme.visibility = View.VISIBLE
            scheme.visibility = View.VISIBLE

            tvScheme.text = schemeName
        }
        lifecycleScope.launch {
            tvManufactureData.text = viewModel.prefHelper.getUserName() ?: ""
        }
        if (pendingOrderItem?.subPartyasRemark.isNotNullOrBlank()) {
            lvlSubParty.text = "Remark     : "
            tvSubParty.text = pendingOrderItem?.subPartyasRemark
        } else {
            lvlSubParty.text = "Sub Party  : "
            tvSubParty.text = pendingOrderItem?.subPartyName ?: "Self"
        }

        binding.regeneratedPdf.setOnClickListener {
            val pendingOrderPDFRegenerateRequest =
                PendingOrderPDFRegenerateRequest.PendingOrderPDFRegenerateRequestItem(
                    pendingOrderItem?.orderID
                )
            viewModel.getPDF(pendingOrderPDFRegenerateRequest)
        }

        viewModel.fetchPdfUrl.observe(viewLifecycleOwner, Observer { pdfUrl ->
            println("GETTING_PDF_URL - $pdfUrl")
            downloadPdfToDownloads(requireContext(), pdfUrl, false)

        })

    }

    private fun registerListeners() = with(binding) {
        btnCloseDialog.setOnClickListener { dismiss() }
        if (pendingOrderItem?.isAdjustedStatus == false) {
            btnEdit.visibility = View.VISIBLE
        } else {
            btnEdit.visibility = View.GONE
        }
        btnEdit.setOnClickListener {

            val bundle = Bundle().apply {
                putString(ARG_PENDING_ORDER_ID, pendingOrderItem?.orderID)
            }
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.pendingorderFragment, false)
                .build()
            findNavController().navigate(R.id.addOrderFragment, bundle, navOptions)
            dismissAllowingStateLoss()
        }



        binding.sharePdf.setOnClickListener {
            downloadPdfToDownloads(requireContext(), pdfUrl, true)
        }

    }

    private fun imageCallBack(url: String) {

        showZoomDialog(
            requireContext(),
            url,
            "image"
        )
      //  showImagePreviewDialog(requireContext(), url)
    }

    private fun showImagePreviewDialog(
        context: Context,
        imageUrl: String
    ) {

        Log.e("TAG", "Image URL: $imageUrl")

        val imageView = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        val dialog = AlertDialog.Builder(context)
            .setView(imageView)
            .setCancelable(false)
            .setPositiveButton("Close") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()

        Glide.with(context)
            .load(imageUrl.trim())
            .placeholder(R.drawable.ic_image1)
            .error(R.drawable.ic_image1)
            .listener(object : RequestListener<Drawable> {

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.d(
                        "TAG",
                        "Image loaded successfully"
                    )

                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.e(
                        "TAG",
                        "Failed URL = $imageUrl"
                    )

                    Log.e(
                        "TAG",
                        "Glide Error = ${e?.message}"
                    )

                    e?.logRootCauses("Glide")

                    Toast.makeText(
                        context,
                        "Image load failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    return false
                }
            })
            .into(imageView)
    }

    @SuppressLint("MissingInflatedId")
    private fun showPdfPreviewDialog(url: String) {
        // dismiss()

        pdfUrl = url
        println("PDF_FILE_PATH 1 $pdfUrl")
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

    // @RequiresApi(Build.VERSION_CODES.R)
    // private fun showPdf(url: String) {
    //     val urlData = url.split("/")
    //     val fileName = urlData.lastOrNull() ?: "downloaded_file.pdf"  // Extract file name from URL
    //     Log.d("TaG", "showPdf: $fileName")
    //
    //     // Use the public downloads directory
    //     val file = File(
    //         Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
    //         fileName
    //     )
    //     if (file.exists()) {
    //         openPdf(file)
    //     } else {
    //         Log.i("TaG", "-----> Download start <--------")
    //
    //         println("CHECKING_THE_PERMISSION ${Environment.isExternalStorageManager()}")
    //
    //
    //     }
    // }

    // private fun downloadPdf(url: String, fileName: String) {
    //     val request = DownloadManager.Request(Uri.parse(url)).apply {
    //         setTitle("Downloading PDF")
    //         setDescription("Please wait while the PDF is being downloaded")
    //
    //         // Use the Downloads directory
    //         setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    //         setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    //     }
    //
    //     downloadManager =
    //         requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    //     downloadId = downloadManager.enqueue(request)
    //
    //     // Create an instance of the custom BroadcastReceiver
    //     val receiver = DownloadCompleteReceiver(
    //         downloadId = downloadId!!,
    //         fileName = fileName,
    //         fragmentReference = WeakReference(requireParentFragment()), // Pass a weak reference to the fragment
    //         downloadManager = downloadManager,
    //         openPdf = { file ->
    //             openPdf(file)
    //             println("FILE_NAME $file")
    //         }
    //     )
    //
    //
    //     // Register the BroadcastReceiver
    //     val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    //         requireContext().registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    //         // openPdf(file)
    //     } else {
    //         requireContext().registerReceiver(receiver, filter)
    //     }
    // }

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
                downloadPdfToDownloads(requireContext(), pdfUrl, false)
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
            downloadPdfToDownloads(requireContext(), pdfUrl, false)
        }
    }


    private fun showZoomDialog(
        context: Context,
        imageUrl: String,
        type: String
    ) {

        val url = imageUrl.trim()

        if (type.equals("image not found", ignoreCase = true) ||
            url.isBlank()
        ) {
            Toast.makeText(
                context,
                "Image not available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        Log.d("IMAGE_DEBUG", "URL=[$url]")

        val dialog = Dialog(
            context,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )

        dialog.setContentView(
            R.layout.dialog_image_zoom
        )

        val photoView =
            dialog.findViewById<PhotoView>(R.id.photoView)

        val btnClose =
            dialog.findViewById<ImageView>(R.id.btnClose)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.empty_photo)
            .error(R.drawable.empty_photo)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.e(
                        "IMAGE_DEBUG",
                        "IMAGE LOAD FAILED"
                    )

                    Log.e(
                        "IMAGE_DEBUG",
                        "URL=[$url]"
                    )

                    Log.e(
                        "IMAGE_DEBUG",
                        "ERROR=${e?.rootCauses}"
                    )

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {

                    Log.d(
                        "IMAGE_DEBUG",
                        "IMAGE LOAD SUCCESS"
                    )

                    return false
                }
            })
            .into(photoView)

        dialog.show()
    }

    // @RequiresApi(Build.VERSION_CODES.R)
    // override fun onRequestPermissionsResult(
    //     requestCode: Int,
    //     permissions: Array<out String>,
    //     grantResults: IntArray
    // ) {
    //     super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    //
    //     when (requestCode) {
    //         STORAGE_PERMISSION_REQUEST_CODE -> {
    //             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    //                 downloadPdfToDownloads(requireContext(), pdfUrl)
    //             }
    //         }
    //
    //     }
    // }

    private fun downloadPdfToDownloads(
        context: Context,
        pdfUrl: String,
        isSharing: Boolean
    ) {

        lifecycleScope.launch {

            try {

                withContext(Dispatchers.IO) {

                    val pdfFile: File =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                            File(
                                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                                pdfUrl.substringAfterLast("/")
                            )

                        } else {

                            File(
                                Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS
                                ),
                                pdfUrl.substringAfterLast("/")
                            )
                        }

                    val url = URL(pdfUrl)

                    val connection =
                        url.openConnection() as HttpURLConnection

                    connection.connectTimeout = 15000
                    connection.readTimeout = 15000
                    connection.connect()

                    val inputStream = BufferedInputStream(
                        connection.inputStream
                    )

                    val outputStream = BufferedOutputStream(
                        FileOutputStream(pdfFile)
                    )

                    val buffer = ByteArray(8 * 1024)

                    var bytesRead: Int

                    while (
                        inputStream.read(buffer).also {
                            bytesRead = it
                        } != -1
                    ) {

                        outputStream.write(buffer, 0, bytesRead)
                    }

                    outputStream.flush()

                    inputStream.close()
                    outputStream.close()

                    withContext(Dispatchers.Main) {

                        if (isSharing) {

                         //   sharePdf(context, pdfFile)

                        } else {

                            openPdf(pdfFile)
                        }
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    context,
                    "Failed to download PDF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001

        fun newInstance(pendingOrderItem: PendingOrderItem): PendingOrderDetailsBottomSheetFragment {
            return PendingOrderDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, pendingOrderItem)
                }
            }
        }
    }
}