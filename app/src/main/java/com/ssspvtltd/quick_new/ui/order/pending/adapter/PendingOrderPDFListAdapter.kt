package com.ssspvtltd.quick_new.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.databinding.ImageListAdapterBinding
import com.ssspvtltd.quick_new.model.order.pending.PendingOrderItem


class PendingOrderPDFListAdapter(private val pdfs: List<PendingOrderItem.PdfPath>?, val mCallBack: (String) -> Unit) : RecyclerView.Adapter<PendingOrderPDFListAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: ImageListAdapterBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ImageListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pdfs?.size ?: 0
        // return 3
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = holder.binding

        binding.image.setImageResource(R.drawable.ic_pdf)
        binding.llImg.setOnClickListener {
            mCallBack( (pdfs?.get(position)?.pdfUrl ?: "").toString())
        }
    }




}
