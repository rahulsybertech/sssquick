package com.ssspvtltd.quick.ui.fairOrder

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R

class ImageAdapter(
    private val images: MutableList<Bitmap>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivImage)
        val delete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.maltiple_image_adapter, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.image.setImageBitmap(images[position])

        holder.delete.setOnClickListener {

            onDelete(position)
        }
    }

    override fun getItemCount() = images.size
}