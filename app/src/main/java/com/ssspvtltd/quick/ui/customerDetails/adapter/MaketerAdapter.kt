package com.ssspvtltd.quick.ui.customerDetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.customerdetails.PersonData


class MaketerAdapter : RecyclerView.Adapter<MaketerAdapter.PersonViewHolder>() {

    private val list = mutableListOf<PersonData>()

    fun submitList(data: List<PersonData>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_adapter, parent, false)
        return PersonViewHolder(view)
    }



    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(list[position],position)
    }

    override fun getItemCount() = list.size

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PersonData, position1: Int) {

            val tvPersonName = itemView.findViewById<TextView>(R.id.tvPersonName)

            tvPersonName.text = "${position1 + 1}. Person Name | ${item.name}"

            val color = when {

                !item.activeStatus -> {
                    ContextCompat.getColor(itemView.context, R.color.red_2)
                }

                item.approvedStatus -> {
                    ContextCompat.getColor(itemView.context, R.color.green)
                }

                else -> {
                    ContextCompat.getColor(itemView.context, R.color.black)
                }
            }

            tvPersonName.setTextColor(color)


        }
    }
}