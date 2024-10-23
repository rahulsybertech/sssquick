package com.ssspvtltd.quick_new.ui.order.add.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.ssspvtltd.quick_new.base.BaseFragment
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateF
import com.ssspvtltd.quick_new.databinding.FragmentSelectImageBinding
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectImageFragment : BaseFragment<FragmentSelectImageBinding,BaseViewModel>() {
    override val inflate: InflateF<FragmentSelectImageBinding>
        get() = FragmentSelectImageBinding::inflate

    override fun initViewModel() = getViewModel<BaseViewModel>()
    private val PICK_IMAGES_REQUEST = 1
    private var selectedImages: MutableList<Uri> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun openGalleryForImages() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else if (data?.data != null) {
                val imageUri: Uri = data.data!!
                selectedImages.add(imageUri)
            }
            binding.imageRecyclerView.adapter?.notifyDataSetChanged() // Notify RecyclerView adapter
        }
    }
}