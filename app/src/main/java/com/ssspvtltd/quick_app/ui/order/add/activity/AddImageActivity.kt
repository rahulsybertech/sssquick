package com.ssspvtltd.quick_app.ui.order.add.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.base.recycler.decoration.GridSpacingItemDecoration
import com.ssspvtltd.quick_app.databinding.ActivityAddImageBinding
import com.ssspvtltd.quick_app.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick_app.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick_app.ui.order.add.adapter.AddImageAdapter
import com.ssspvtltd.quick_app.ui.order.add.viewmodel.AddImageViewModel
import com.ssspvtltd.quick_app.utils.extension.dp
import com.ssspvtltd.quick_app.utils.extension.getViewModel
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
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            if (it) mAdapter.submitList(viewModel.widgetList)
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 16.dp, 16.dp))
    }

    private fun registerListener() {
        mAdapter.addImageListener = {
            // imageUri = MediaUtils.getBlankImageUri(this@AddImageActivity)
            // getCameraImage.launch(imageUri)
            getGalleryImage.launch("image/*")
        }
        mAdapter.deleteImageListener = { image, position ->
            viewModel.deleteFileFromList(image, position)
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sendResultBack()
            }
        })
    }

    private fun sendResultBack() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putParcelableArrayListExtra(ARG_ADD_IMAGE_LIST, viewModel.getImageModelList())
        })
        finish()
    }

    private val getGalleryImage = registerForActivityResult(GetMultipleContents()) { uris ->
        if (uris?.isNotEmpty() == true) {
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