package com.ssspvtltd.quick.ui.order.add.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.base.recycler.decoration.GridSpacingItemDecoration
import com.ssspvtltd.quick.databinding.ActivityAddImageBinding
import com.ssspvtltd.quick.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.ui.order.add.adapter.AddImageAdapter
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddImageViewModel
import com.ssspvtltd.quick.utils.SharedEditImageUriList
import com.ssspvtltd.quick.utils.extension.dp
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddImageActivity : BaseActivity<ActivityAddImageBinding, AddImageViewModel>() {
    private var imageUri: Uri? = null
    private val mAdapter by lazy { AddImageAdapter() }

    override val inflate: InflateA<ActivityAddImageBinding> get() = ActivityAddImageBinding::inflate
    override fun initViewModel(): AddImageViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        registerObserver()
        registerListener()
        viewModel.prepareList()
        setImageUri()
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            if (it) mAdapter.submitList(viewModel.widgetList)
            if(viewModel.widgetList.size>1){
                binding.btnUploadImages.isEnabled = true
                binding.btnUploadImages.alpha = 1f
            }else{
                binding.btnUploadImages.isEnabled = false
                binding.btnUploadImages.alpha = 0.5f
            }

        }



    }
    private fun setImageUri() {
        val uris = SharedEditImageUriList.imageUris
        if (uris?.isNotEmpty() == true) {
            viewModel.setEditImageList(uris)
            //viewModel.addFilesToList(*uris.toTypedArray())
        } else {
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 5.dp, 5.dp))
    }

    private fun registerListener() {
        mAdapter.addImageListener = {
            // imageUri = MediaUtils.getBlankImageUri(this@AddImageActivity)
            // getCameraImage.launch(imageUri)
            if (viewModel.getImageModelList().size >= 5) {
                Toast.makeText(this, "You can select a maximum of 5 images.", Toast.LENGTH_SHORT).show()
            } else {
                getGalleryImage.launch("image/*")
            }
        }
        mAdapter.deleteImageListener = { image, position ->
            if(SharedEditImageUriList.imageUris?.contains(Uri.parse(image.filePath)) == true) {
                SharedEditImageUriList.imageUris = SharedEditImageUriList.imageUris?.filter { it != Uri.parse(image.filePath) }?.toTypedArray()?.toList()
            }
            viewModel.deleteFileFromList(image, position)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sendResultBack()
            }
        })

        binding.btnUploadImages.isEnabled = false
        binding.btnUploadImages.alpha = 0.5f

        binding.btnUploadImages.setOnClickListener {
            sendResultBack()

        }
    }


    private fun sendResultBack() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putParcelableArrayListExtra(ARG_ADD_IMAGE_LIST, viewModel.getImageModelList())
        })
        finish()
    }

    private val getGalleryImage = registerForActivityResult(GetMultipleContents()) { uris ->
        if (uris?.isNotEmpty() == true) {
            binding.btnUploadImages.isEnabled = true
            binding.btnUploadImages.alpha = 1f
            val fileLimit = 5 - viewModel.getImageModelList().size
            val validUris = uris.take(fileLimit).filter { uri ->
                val fileSize = getFileSize(uri)
                if (fileSize > (2 * 1024 * 1024)) { // Check if file size is greater than 2MB
                    Toast.makeText(this, "selected image size should not more then 2MB.", Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }

            if (validUris.isNotEmpty()) {
                viewModel.addFilesToList(*validUris.toTypedArray())
            } else {
                //Toast.makeText(this, "No files meet the size requirement (max 2MB).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                cursor.moveToFirst()
                fileSize = cursor.getLong(sizeIndex)
            }
        }
        return fileSize
    }


    private val getCameraImage = registerForActivityResult(TakePicture()) {
        if (it == true && imageUri != null) {
            viewModel.addFilesToList(imageUri!!)
        }
    }

    companion object {
        private const val TAG = "AddImageActivity"

        @JvmStatic
        fun newStartIntent(context: Context, list: ArrayList<ImageModel>?): Intent {
            return Intent(context, AddImageActivity::class.java).apply {
                putParcelableArrayListExtra(ARG_ADD_IMAGE_LIST, list)
            }
        }
    }


}
