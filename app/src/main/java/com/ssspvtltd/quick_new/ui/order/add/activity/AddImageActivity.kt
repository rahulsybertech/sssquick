package com.ssspvtltd.quick_new.ui.order.add.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import com.ssspvtltd.quick_new.base.BaseActivity
import com.ssspvtltd.quick_new.base.InflateA
import com.ssspvtltd.quick_new.base.recycler.decoration.GridSpacingItemDecoration
import com.ssspvtltd.quick_new.databinding.ActivityAddImageBinding
import com.ssspvtltd.quick_new.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick_new.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick_new.ui.order.add.adapter.AddImageAdapter
import com.ssspvtltd.quick_new.ui.order.add.viewmodel.AddImageViewModel
import com.ssspvtltd.quick_new.utils.extension.dp
import com.ssspvtltd.quick_new.utils.extension.getViewModel
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

    private val getGalleryImage = registerForActivityResult(GetMultipleContents()) {
        if (it?.isNotEmpty() == true) {
            viewModel.addFilesToList(*it.toTypedArray())
        }
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