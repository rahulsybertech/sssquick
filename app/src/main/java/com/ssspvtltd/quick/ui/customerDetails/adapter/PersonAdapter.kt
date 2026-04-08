package com.ssspvtltd.quick.ui.customerDetails.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.model.customerdetails.PersonModel
import com.ssspvtltd.quick.utils.showToast

class PersonAdapter(
    private val list: MutableList<PersonModel>,
    private val onAddClick: (Int) -> Unit,
    private val onRemoveClick: (Int) -> Unit,
    private val onAadharFrontClick: (Int) -> Unit,
    private val onAadharBackClick: (Int) -> Unit,
    var onRequestNextFocus: ((Int) -> Unit)? = null,
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val etName: EditText = itemView.findViewById(R.id.etPersonName)
      //  val layoutDelete: FrameLayout = itemView.findViewById(R.id.layoutDelete)
   //     val layoutEdit: FrameLayout = itemView.findViewById(R.id.layoutEdit)
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

        // 🔥 Remove previous watcher
        if (holder.etName.tag is TextWatcher) {
            holder.etName.removeTextChangedListener(holder.etName.tag as TextWatcher)
        }

        holder.etName.setText(item.personName ?: "")

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                list[holder.adapterPosition].personName = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        holder.etName.addTextChangedListener(watcher)

        // 🔥 Save watcher in tag
        holder.etName.tag = watcher

        if(item.aadharFrontBitmap!=null){
            holder.imgFront.setImageBitmap(item.aadharFrontBitmap)

        }else{
            Glide.with(holder.imgFront.context)
                .load(item.frontURL).error(R.drawable.empty_photo)
                .signature(ObjectKey(System.currentTimeMillis()))
                .into(holder.imgFront)


        }
        if(item.aadharBackBitmap==null){

            Glide.with( holder.imgBack)
                .load(item.backURL).
                error(R.drawable.empty_photo)
                    .signature(ObjectKey(System.currentTimeMillis()))
                .into( holder.imgBack)

        }else{
            holder.imgBack.setImageBitmap(item.aadharBackBitmap)
        }

        holder.btnAdd.setOnClickListener {
            if (item.personName.isEmpty()) {
                showToast("Please Enter Person Name")
            } else if (item.personName.length > 30) {
                showToast("Person Name should not be greater than 30 characters")
            }
            else if (item.frontURL.isNotEmpty() || item.aadharFrontBase64?.isNotEmpty() == true||item.backURL.isNotEmpty() || item.aadharBackBase64?.isNotEmpty() == true) {
                onAddClick(position)
                onRequestNextFocus?.invoke(position + 1)
            }
            else {
                showToast("At Least one person's photo is required.")
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
        val currentPosition = holder.bindingAdapterPosition

        if (currentPosition != RecyclerView.NO_POSITION) {
            holder.btnAdd.visibility =
                if (currentPosition == list.size - 1) View.VISIBLE else View.GONE
        } else {
            holder.btnAdd.visibility = View.GONE
        }
        holder.btnRemove.visibility =
            if (list.size == 1) View.GONE else View.VISIBLE
    }
    fun updateList(newList: List<PersonModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}