package com.ssspvtltd.quick.ui.customerDetails.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.customerdetails.PersonModel
import com.ssspvtltd.quick.utils.showToast

class PersonAdapter(
    private val list: MutableList<PersonModel>,
    private val onAddClick: (Int) -> Unit,
    private val onRemoveClick: (Int) -> Unit,
    private val onAadharFrontClick: (Int) -> Unit,
    private val onAadharBackClick: (Int) -> Unit,
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val etName: EditText = itemView.findViewById(R.id.etPersonName)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)
        val imgFront: ImageView = itemView.findViewById(R.id.imgFront)
        val imgBack: ImageView = itemView.findViewById(R.id.imgBack)
        val btnRemove: ImageView = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.etName.setText(item.personName)
        holder.imgFront.setImageBitmap(item.aadharFrontBitmap)
        holder.imgBack.setImageBitmap(item.aadharBackBitmap)

        holder.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.personName = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.btnAdd.setOnClickListener {
            if (item.personName.isNotEmpty()&&item.aadharFrontBase64!!.isNotEmpty()) {
                onAddClick(position)
            }else if(item.personName.isEmpty()){
                showToast("Please Enter Person Name")
            }
            else{
                showToast("At least one person's photo is required.")
            }
        }

        holder.btnRemove.setOnClickListener {
            onRemoveClick(position)
        }
        holder.imgFront.setOnClickListener {
            onAadharFrontClick(position)
        }
        holder.imgBack.setOnClickListener {
            onAadharBackClick(position)
        }

        // Hide delete if only 1 item
        holder.btnRemove.visibility =
            if (list.size == 1) View.GONE else View.VISIBLE
    }
}