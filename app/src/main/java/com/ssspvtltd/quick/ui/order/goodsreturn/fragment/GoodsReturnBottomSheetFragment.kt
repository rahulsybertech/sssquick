package com.ssspvtltd.quick.ui.order.goodsreturn.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentGoodReturnBottomSheetBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ITEM
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnItem
import com.ssspvtltd.quick.ui.create_gr.CreateGRActivity
import com.ssspvtltd.quick.utils.DateTimeFormat
import com.ssspvtltd.quick.utils.DateTimeUtils
import com.ssspvtltd.quick.utils.SharedEditImageUriList
import com.ssspvtltd.quick.utils.extension.getParcelableExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class GoodsReturnBottomSheetFragment :
    BaseBottomDialog<FragmentGoodReturnBottomSheetBinding, BaseViewModel>() {
    private var id: String? = null
    private var goodsReturnItem: GoodsReturnItem? = null
    private var selectedSlot: Int = -1
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val imageBase64List = arrayOfNulls<String>(3)
    private lateinit var imageFromGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageFromCameraLauncher: ActivityResultLauncher<Uri>
    private var editDataGr: GoodsReturnDataGr? = null
    private var cameraImageUri: Uri? = null

    override val inflate: InflateBD<FragmentGoodReturnBottomSheetBinding>
        get() = FragmentGoodReturnBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            goodsReturnItem = it.getParcelable(ARG_PENDING_ORDER_ITEM)
            id = it.getString(ARG_ID)
        }
        imageFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                handleImageResult(uri)
            }
        }

        imageFromCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                handleImageResult(cameraImageUri!!)
            }
        }


        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val base64 = convertImageToBase64(requireContext(), uri)

                when (selectedSlot) {
                    0 -> updateSlot(binding.imgPreview1, binding.btnAddImage1, binding.btnRemoveImage1, uri, base64, 0)
                    1 -> updateSlot(binding.imgPreview2, binding.btnAddImage2, binding.btnRemoveImage2, uri, base64, 1)
                    2 -> updateSlot(binding.imgPreview3, binding.btnAddImage3, binding.btnRemoveImage3, uri, base64, 2)
                }
            }
        }



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
        val amount=  goodsReturnItem?.amount?.toString() ?: "0"
        tvAmount.text=amount
        tvPcs.text=goodsReturnItem?.qty.toString()
       val changedQty=  goodsReturnItem?.changedQty?.toString() ?: "0"
        tvPcsChange.text=changedQty
        val displayQty = goodsReturnItem?.noChangeQty?.toString() ?: "0"
        tvPcsNoChange.text=displayQty
        tvRemark.text=goodsReturnItem?.remark ?: "N/A"
         binding.btnUploadImages.isEnabled = false
        binding.btnUploadImages.alpha = 0.5f
        btnEdit.setOnClickListener {
        /*    val bundle = Bundle().apply {
                putString(ARG_PENDING_ORDER_ID, goodsReturnItem!!.id)
            }*/
            val intent = Intent(requireContext(), CreateGRActivity::class.java).apply {
                putExtra(ARG_PENDING_ORDER_ID, goodsReturnItem!!.id)
            }
            startActivity(intent)
            dismissAllowingStateLoss()
        }





        binding.btnAddImage1.setOnClickListener {
            selectedSlot = 0
            pickImage()
        }
        binding.btnAddImage2.setOnClickListener {
            selectedSlot = 1
            pickImage()
        }
        binding.btnAddImage3.setOnClickListener {
            selectedSlot = 2
            pickImage()
        }

        binding.btnAddImage1.setOnClickListener { selectedSlot = 0; showImagePickerOptions() }
        binding.btnAddImage2.setOnClickListener { selectedSlot = 1; showImagePickerOptions() }
        binding.btnAddImage3.setOnClickListener { selectedSlot = 2; showImagePickerOptions() }

        binding.btnRemoveImage1.setOnClickListener {
            clearSlot(binding.imgPreview1, binding.btnAddImage1, binding.btnRemoveImage1, 0)
        }
        binding.btnRemoveImage2.setOnClickListener {
            clearSlot(binding.imgPreview2, binding.btnAddImage2, binding.btnRemoveImage2, 1)
        }
        binding.btnRemoveImage3.setOnClickListener {
            clearSlot(binding.imgPreview3, binding.btnAddImage3, binding.btnRemoveImage3, 2)
        }

        binding.btnUploadImages.setOnClickListener {
            val validBase64List = imageBase64List.filterNotNull()
            if (validBase64List.isEmpty()) {
                Toast.makeText(requireContext(), "Please upload at least 1 image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val image1 = imageBase64List.getOrNull(0)
            val image2 = imageBase64List.getOrNull(1)
            val image3 = imageBase64List.getOrNull(2)

            when {
                // If image 3 is selected, but 1 or 2 is missing
                image3 != null && (image1 == null || image2 == null) -> {
                    val id = id ?: return@setOnClickListener
                    viewModel.uploadGoodsReturnImages(
                        id,
                        image3,
                        image2,
                        image1
                    )  }

                // If image 2 is selected but image 1 is missing
                image2 != null && image1 == null -> {
                    val id = id ?: return@setOnClickListener
                    viewModel.uploadGoodsReturnImages(
                        id,
                        image2,
                        image1,
                        image3
                    )
                }

                else -> {
                    // Proceed with upload
                    val id = id ?: return@setOnClickListener
                    viewModel.uploadGoodsReturnImages(
                        id,
                        image1,
                        image2,
                        image3
                    )
                }
            }

        }

        viewModel.isUploading.observe(viewLifecycleOwner) { isUploading ->
            binding.progressBar.visibility = if (isUploading) View.VISIBLE else View.GONE
            binding.btnUploadImages.isEnabled = !isUploading
        }

        viewModel.uploadSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                // Clear UI after success
                clearSlot(binding.imgPreview1, binding.btnAddImage1, binding.btnRemoveImage1, 0)
                clearSlot(binding.imgPreview2, binding.btnAddImage2, binding.btnRemoveImage2, 1)
                clearSlot(binding.imgPreview3, binding.btnAddImage3, binding.btnRemoveImage3, 2)

                // Optional: dismiss bottom sheet or notify parent
                dismiss()
            }
        }

        viewModel.getEditOrderGr(id!!)
        viewModel.editOrderGr.observe(viewLifecycleOwner) { grData ->
            lifecycleScope.launch {
                try {
                    val grinImages = grData?.itemDetailsImageList
                        ?.filter { it.documentType == "GRSTATUS" }
                        ?.take(3)

                    Log.d("GRIN_IMAGES", "Found GRIN images: ${grinImages?.size}")
                    tvSaleBillDate.text = DateTimeUtils.formatDate(
                        grData?.grInDate, DateTimeFormat.DATE_TIME_FORMAT1,
                        DateTimeFormat.DATE_TIME_FORMAT3
                    )

                  //  tvSaleBillDate.text=grData?.grInDate?:"N?A" // pass through apapter constructor

                    val imageUrlList = grinImages?.mapNotNull { it.imagePath } ?: emptyList()
                    imageUrlList.getOrNull(0)?.let {
                        binding.imgPreview1.visibility = View.VISIBLE
                        convertImageUrlToBase64(it) { base64 ->
                            if (base64 != null) {
                                imageBase64List[0] = base64
                                Log.d("BASE64", "Encoded: $base64")
                                // use base64 here
                            } else {
                           //     Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                            }
                        }


                        Glide.with(requireContext()).load(it).into(binding.imgPreview1)
                        binding.btnAddImage1.visibility = View.GONE
                        binding.btnRemoveImage1.visibility = View.VISIBLE
                    }

                    imageUrlList.getOrNull(1)?.let {
                        binding.imgPreview2.visibility = View.VISIBLE
                        convertImageUrlToBase64(it) { base64 ->
                            if (base64 != null) {
                                imageBase64List[1] = base64
                                Log.d("BASE64", "Encoded: $base64")
                                // use base64 here
                            } else {
                                //     Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Glide.with(requireContext()).load(it).into(binding.imgPreview2)
                        binding.btnAddImage2.visibility = View.GONE
                        binding.btnRemoveImage2.visibility = View.VISIBLE
                    }

                    imageUrlList.getOrNull(2)?.let {
                        binding.imgPreview3.visibility = View.VISIBLE
                        convertImageUrlToBase64(it) { base64 ->
                            if (base64 != null) {
                                imageBase64List[2] = base64
                                Log.d("BASE64", "Encoded: $base64")
                                // use base64 here
                            } else {
                                //     Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Glide.with(requireContext()).load(it).into(binding.imgPreview3)
                        binding.btnAddImage3.visibility = View.GONE
                        binding.btnRemoveImage3.visibility = View.VISIBLE
                    }



                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }




    }

    companion object {
        private const val ARG_PENDING_ORDER_ITEM = "arg_pending_order_item"
        private const val ARG_ID = "arg_id"
        fun newInstance(goodsReturnItem: GoodsReturnItem,value:String): GoodsReturnBottomSheetFragment {

            return GoodsReturnBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PENDING_ORDER_ITEM, goodsReturnItem)
                    putString(ARG_ID, value)
                }
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }
    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Choose image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                }
            }
            .show()
    }
    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1001)
        } else {
            openCamera()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openCamera() {
        val photoFile = File.createTempFile("IMG_", ".jpg", requireContext().cacheDir)
        val authority = "com.ssspvtltd.quick.provider"

        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            authority,
            photoFile
        )

        imageFromCameraLauncher.launch(cameraImageUri!!)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imageFromGalleryLauncher.launch(intent)
    }
    private fun handleImageResult(uri: Uri) {
        val base64 = convertImageToBase64(requireContext(), uri)

        when (selectedSlot) {
            0 -> updateSlot(binding.imgPreview1, binding.btnAddImage1, binding.btnRemoveImage1, uri, base64, 0)
            1 -> updateSlot(binding.imgPreview2, binding.btnAddImage2, binding.btnRemoveImage2, uri, base64, 1)
            2 -> updateSlot(binding.imgPreview3, binding.btnAddImage3, binding.btnRemoveImage3, uri, base64, 2)
        }
    }




    private fun updateSlot(
        imgView: ImageView,
        addIcon: ImageView,
        closeIcon: ImageView,
        uri: Uri,
        base64: String,
        index: Int
    ) {
        imgView.setImageURI(uri)
        imgView.visibility = View.VISIBLE
        addIcon.visibility = View.GONE
        closeIcon.visibility = View.VISIBLE
        imageBase64List[index] = base64
        binding.btnUploadImages.isEnabled = true
        // Enable and style the button
        binding.btnUploadImages.isEnabled = true
        binding.btnUploadImages.alpha = 1f
      //  binding.btnUploadImages.setBackgroundColor(ContextCompat.getColor(requireContext(), R.d.red_2)) // ✅ Green
        binding.btnUploadImages.setTextColor(Color.WHITE) // optional
        viewModel.updateSelectedImages(imageBase64List.toList())
    }

    private fun clearSlot(
        imgView: ImageView,
        addIcon: ImageView,
        closeIcon: ImageView,
        index: Int
    ) {
        binding.btnUploadImages.isEnabled = true
        binding.btnUploadImages.alpha = 1f
        imgView.setImageDrawable(null)
        imgView.visibility = View.GONE
        addIcon.visibility = View.VISIBLE
        closeIcon.visibility = View.GONE
        imageBase64List[index] = null
    }

    private fun convertImageToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun convertImageUrlToBase64(imageUrl: String, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val base64 = try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Mozilla/5.0")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.doInput = true
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream: InputStream = connection.inputStream
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                } else {
                    Log.e("BASE64", "HTTP Error: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("BASE64", "Exception: ", e)
                null
            }

            // Return result on main thread
            withContext(Dispatchers.Main) {
                callback(base64)
            }
        }
    }



}