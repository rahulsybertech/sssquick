package com.ssspvtltd.quick.ui.customerDetails.allAccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.customerdetails.Person
import com.ssspvtltd.quick.model.customerdetails.PersonData

class PersonAdapter : RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

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
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PersonData) {

            val tvPersonName = itemView.findViewById<TextView>(R.id.tvPersonName)

            tvPersonName.text = "Person Name | ${item.name}"

            val color = if (item.activeStatus
                ) {
                ContextCompat.getColor(itemView.context, R.color.black)
            } else {
                ContextCompat.getColor(itemView.context, R.color.red_2)
            }

            tvPersonName.setTextColor(color)
        }
    }
}


