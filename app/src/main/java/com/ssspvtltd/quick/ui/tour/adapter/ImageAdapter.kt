package com.ssspvtltd.quick.ui.tour.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R

class ImageAdapter(
    private val imageList: MutableList<Bitmap>,
    private val onDelete: (Int) -> Unit,
    private val onAdd: () -> Unit,
    private val maxCount: Int
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

            holder.imgPhoto.setImageBitmap(imageList[position])

            holder.imgDelete.visibility = View.VISIBLE

            holder.imgDelete.setOnClickListener {
                onDelete(position)
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