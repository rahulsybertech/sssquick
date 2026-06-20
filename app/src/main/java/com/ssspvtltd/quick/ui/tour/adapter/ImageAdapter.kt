package com.ssspvtltd.quick.ui.tour.adapter

import android.app.Dialog
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.ui.tour.model.ImageItem

class ImageAdapter(
    private val imageList: MutableList<ImageItem>,
    private val onDelete: (Int) -> Unit,
    private val onAdd: () -> Unit,
    private val zoomIN: (position: Int, mobile: ImageItem,type:String) -> Unit,
    private val maxCount: Int,

) : RecyclerView.Adapter<ImageAdapter.ImageVH>() {

    inner class ImageVH(view: View) : RecyclerView.ViewHolder(view) {

        val imgPhoto = view.findViewById<ImageView>(R.id.imgPhoto)
        val imgDelete = view.findViewById<ImageView>(R.id.imgDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_adapter, parent, false)

        return ImageVH(view)
    }

    override fun getItemCount(): Int {

        return if (imageList.size < maxCount) {
            imageList.size + 1
        } else {
            imageList.size
        }
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {

        if (position < imageList.size) {

            val item = imageList[position]

            when {

                item.bitmap != null -> {
                    holder.imgDelete.visibility = View.VISIBLE
                    holder.imgPhoto.setImageBitmap(item.bitmap)
                }

                item.imageUrl != null -> {
                    holder.imgDelete.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(item.imageUrl)
                        .into(holder.imgPhoto)
                }
            }

            holder.imgDelete.setOnClickListener {
                onDelete(position)
            }

            holder.imgPhoto.setOnClickListener {
                val pos = holder.bindingAdapterPosition

                when {

                    item.bitmap!=null -> {

                        zoomIN(
                            pos,
                            item,
                            "noturl"
                        )
                    }

                    item.imageUrl!=null -> {

                        zoomIN(
                            pos,
                            item,
                            "url"
                        )
                    }

                    else -> {

                        zoomIN(pos, item, "image not found")
                    }
                }
            }

        } else {

            holder.imgPhoto.setImageResource(R.drawable.ic_baseline_plus_24)

            holder.imgDelete.visibility = View.GONE

            holder.imgPhoto.setOnClickListener {
                onAdd()
            }
        }
    }
}