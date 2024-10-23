package com.ssspvtltd.quick_new.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick_new.databinding.ImageListAdapterBinding
import com.ssspvtltd.quick_new.model.order.pending.PendingOrderItem


class PendingOrderImageListAdapter(private val imgs: List<PendingOrderItem.ImagePath>?, val mCallBack: (String) -> Unit) : RecyclerView.Adapter<PendingOrderImageListAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ImageListAdapterBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ImageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imgs?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding

        binding.llImg.setOnClickListener {
            mCallBack( (imgs?.get(position)?.url ?: "").toString())
        }
    }




}
