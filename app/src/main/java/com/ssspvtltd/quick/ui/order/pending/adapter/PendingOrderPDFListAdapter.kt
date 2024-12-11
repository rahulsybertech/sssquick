package com.ssspvtltd.quick.ui.order.pending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.commonrepo.CommonPDFRepo
import com.ssspvtltd.quick.databinding.ImageListAdapterBinding
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.model.order.pending.PendingOrderItem
import com.ssspvtltd.quick.model.progress.ProgressConfig
import com.ssspvtltd.quick.networking.ResultWrapper
import com.ssspvtltd.quick.ui.order.pending.repository.PendingOrderRepository
import kotlinx.coroutines.launch


class PendingOrderPDFListAdapter(private val pdfs: List<PendingOrderItem.PdfPath>?, val mCallBack: (String) -> Unit) : RecyclerView.Adapter<PendingOrderPDFListAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ImageListAdapterBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: String, onItemClick: (String) -> Unit) {
            itemView.setOnClickListener { onItemClick(item) }
        }
    }


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

            //api call


        }
    }
}
