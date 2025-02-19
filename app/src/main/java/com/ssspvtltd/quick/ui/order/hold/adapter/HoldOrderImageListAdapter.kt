package com.ssspvtltd.quick.ui.order.hold.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.databinding.ImageListAdapterBinding


class HoldOrderImageListAdapter(private val imgs: List<com.ssspvtltd.quick.model.order.hold.HoldOrderItem.ImagePath>?, val mCallBack: (String) -> Unit) : RecyclerView.Adapter<HoldOrderImageListAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ImageListAdapterBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ImageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imgs?.size ?: 0
        // return 3
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding

        if((imgs?.get(position)?.url ?: "").contains(".pdf")){
            binding.image.setAnimation(R.raw.pdf_ani)
        } else {
            binding.image.setImageResource(R.drawable.ic_image)
        }

        binding.llImg.setOnClickListener {
            mCallBack( (imgs?.get(position)?.url ?: "").toString())
        }
    }








}
