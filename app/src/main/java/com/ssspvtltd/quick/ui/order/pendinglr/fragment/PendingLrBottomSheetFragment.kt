package com.ssspvtltd.quick.ui.order.pendinglr.fragment

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
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ssspvtltd.quick.BuildConfig
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentPendingLrBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.order.pendinglr.PendingLrItem

import com.ssspvtltd.quick.utils.DownloadCompleteReceiver
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class PendingLrBottomSheetFragment :
    BaseBottomDialog<FragmentPendingLrBottomSheetBinding, BaseViewModel>() {
    private var pendingLrItem: PendingLrItem? = null
    private var pdfUrl = ""
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
        tvSaleBillDate.text=pendingLrItem?.saleBillDate ?: "N/A"
        tvQuantity.text=pendingLrItem?.qty.toString()


        if(pendingLrItem?.wayBillNo!!.isEmpty()){
            wayWillImage.visibility=View.GONE
        }else{
            wayWillImage.visibility=View.VISIBLE
        }
        if(pendingLrItem?.billNo!!.isEmpty()){
            ivDoc.visibility=View.GONE
        }else{
            ivDoc.visibility=View.VISIBLE
        }

        if (pendingLrItem?.saleBillPdf.isNullOrEmpty()) {
            ivDoc.visibility = View.GONE
        } else {
            ivDoc.visibility = View.VISIBLE
        }
        if(pendingLrItem?.wayBillPdf.isNullOrEmpty()){
            wayWillImage.visibility=View.GONE
        }else{
            wayWillImage.visibility=View.VISIBLE
        }
        binding.sharePdf.visibility = if (pendingLrItem?.saleBillPdf.isNullOrEmpty() &&
            pendingLrItem?.wayBillPdf.isNullOrEmpty()) {
            View.GONE  // Hide if both are null or empty
        } else {
            View.VISIBLE  // Show if at least one is not null/empty
        }

         // tvItem.text=pendingLrItem?.itemName ?: "N/A"
         // tvStatus.text=pendingLrItem?.status ?: "N/A"
         tvSaleBillNo.text=pendingLrItem?.billNo ?: "N/A"
        saleBillNumber.text=pendingLrItem?.billNo ?: "N/A"
        wayBillNumber.text=pendingLrItem?.wayBillNo ?: "N/A"
         tvAmount.text=pendingLrItem?.amount.toString()
        // tvPcs.text=pendingLrItem?.qty.toString()
        //  tvRemark.text=pendingLrItem?.remark ?: "N/A"A
        //  tvSaleBillDate.text=pendingLrItem? // pass through apapter constructor


        sharePdf.setOnClickListener {
            var url=  pendingLrItem?.imagePaths
            val myList = mutableListOf<String>()  // Creating an empty mutable list

            pendingLrItem?.wayBillPdf?.let {
                if (it.isNotEmpty()) {
                    myList.add(it)
                }
            }

            pendingLrItem?.saleBillPdf?.let {
                if (it.isNotEmpty()) {
                    myList.add(it)
                }
            }
            if(myList.size>0){
                downloadAndShareMultiplePdfs(requireContext(), myList, true)
            }else{
                Toast.makeText(context, "Pdf not available ", Toast.LENGTH_SHORT).show()
            }
            // downloadPdfToDownloads(requireContext(), url!!, true)

        }

        llDoc.setOnClickListener {
            /*   if ((stockInOfficeItem?.imagePaths ?: "").contains(".pdf")) {
                   showPdfPreviewDialog(stockInOfficeItem?.imagePaths ?: "")
               } else {
                   showImagePreviewDialog(requireContext(), stockInOfficeItem?.imagePaths ?: "")
               } */
            if ((pendingLrItem?.saleBillPdf ?: "").contains(".pdf")) {
                showPdfPreviewDialog(pendingLrItem?.saleBillPdf ?: "")
            } else {
             //   showImagePreviewDialog(requireContext(), pendingLrItem?.imagePaths ?: "")
            }
        }

        wayWillDocLL.setOnClickListener {

            if ((pendingLrItem?.wayBillPdf ?: "").contains(".pdf")) {
                showPdfPreviewDialog(pendingLrItem?.wayBillPdf ?: "")
            } else {
              //  showImagePreviewDialog(requireContext(), pendingLrItem?.imagePaths ?: "")
            }
        }

    }

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
        fun newInstance(pendingLrItem: PendingLrItem): PendingLrBottomSheetFragment {
            return PendingLrBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, pendingLrItem)
                }
            }
        }
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
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
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
        //dismiss()
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
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        if (file.exists()) {
            openPdf(file)
        } else {
            Log.i("TaG","-----> Download start <--------")
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

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)

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
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showPdf(pdfUrl)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
            }
        } else {
            showPdf(pdfUrl)
        }
    }

    private fun downloadAndShareMultiplePdfs(context: Context, pdfUrls: List<String>, isSharing: Boolean) {
        val pdfFiles = mutableListOf<File>()

        // Step 1: Download each PDF and store the file path
        pdfUrls.forEach { pdfUrl ->
            println("PDF_FILE_PATH 0 $pdfUrl")

            val pdfFile: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    pdfUrl.substringAfterLast("/")
                )
            } else {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    pdfUrl.substringAfterLast("/")
                )
            }

            // Step 2: Download the file
            try {
                StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())
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

                // Add the downloaded file to the list
                pdfFiles.add(pdfFile)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to download PDF from $pdfUrl", Toast.LENGTH_SHORT).show()
                return
            }
            println("PDF_FILE_PATH $pdfFile")
        }

        // Step 3: Share the PDFs if isSharing is true
        if (isSharing) {
            try {
                val uris = pdfFiles.map { pdfFile ->
                    FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        pdfFile
                    )
                }

                // Create the intent to share multiple PDFs
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "*/*"  // Set the MIME type to handle PDF files
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Handle opening each PDF (you can modify this logic to handle multiple PDFs opening if needed)
            pdfFiles.forEach { pdfFile ->
                openPdfMaltipleUrl(pdfFile)
            }
        }
    }

    private fun openPdfMaltipleUrl(pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            requireContext().startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }
}