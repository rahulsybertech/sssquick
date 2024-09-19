package com.ssspvtltd.quick.ui.order.add.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.ItemAddImageBinding
import com.ssspvtltd.quick.model.ImageViewType
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel

class AddImageAdapter : MultiViewAdapter() {
    internal var addImageListener: (() -> Unit)? = null
    internal var deleteImageListener: ((ImageModel) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            ImageViewType.ADD_IMAGE.id -> {
                val binding = ItemAddImageBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                binding.imageView.setImageResource(R.drawable.ic_baseline_plus_24)
                binding.imageView.scaleType = ImageView.ScaleType.CENTER
                binding.btnDelete.isVisible = false
                return BaseViewHolder(binding).apply {
                    itemView.setOnClickListener { addImageListener?.invoke() }
                }
            }

            ImageViewType.IMAGE.id -> {
                val binding = ItemAddImageBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                binding.btnDelete.isVisible = true
                return ImageViewHolder(binding).apply {
                    this.binding.btnDelete.setOnClickListener {
                        getItemOrNull<ImageModel>(bindingAdapterPosition)?.let {
                            deleteImageListener?.invoke(it)
                        }
                    }
                }
            }

            else -> {
                return super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> getItemOrNull<ImageModel>(position)?.let(holder::bind)
            else -> super.onBindViewHolder(holder, position)
        }
    }
}

class ImageViewHolder(val binding: ItemAddImageBinding) : BaseViewHolder(binding) {
    fun bind(imageModel: ImageModel) = with(binding) {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(imageView).load(imageModel.filePath).into(imageView)
    }
}