package com.ssspvtltd.quick.ui.customerDetails.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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


        // ✅ Single TextWatcher (FIXED)
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val text = s.toString()

                // 🚫 Prevent space at start
                if (text.startsWith(" ")) {
                    val trimmed = text.trimStart()
                    holder.etName.setText(trimmed)
                    holder.etName.setSelection(trimmed.length)
                    return
                }

                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    list[pos].personName = text
                }

                // ✅ Length validation
                if (text.length > 35) {
                    holder.etName.error = "Max 35 characters allowed"
                } else {
                    holder.etName.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        holder.etName.addTextChangedListener(watcher)

        // 🔥 Save watcher in tag
        holder.etName.tag = watcher

        if(item.aadharFrontBitmap!=null){
            Glide.with(holder.itemView.context)
                .load(item.aadharFrontBitmap)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imgFront)

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
            Glide.with(holder.itemView.context).clear(holder.imgBack)

            Glide.with(holder.itemView.context)
                .load(item.aadharBackBitmap)
                .placeholder(R.drawable.empty_photo)
                .into(holder.imgBack)
        }

        holder.btnAdd.setOnClickListener {
            if (item.personName.isEmpty()) {
                showToast("Please Enter Person Name")
            }/* else if (item.personName.length > 30) {
                showToast("Person Name should not be greater than 30 characters")
            }*/
            else if (item.frontURL.isNotEmpty() || item.aadharFrontBase64?.isNotEmpty() == true||item.backURL.isNotEmpty() || item.aadharBackBase64?.isNotEmpty() == true) {
                onAddClick(position)
                onRequestNextFocus?.invoke(position + 1)
            }
            else {
                showToast("At Least one Aadhar photo is required.")
            }
        }
        holder.etName.setOnEditorActionListener { v, actionId, _ ->

            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT ||
                actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                val nextPos = holder.bindingAdapterPosition + 1

                val recyclerView = holder.itemView.parent as RecyclerView

                if (nextPos < list.size) {

                    recyclerView.post {
                        recyclerView.smoothScrollToPosition(nextPos)

                        recyclerView.findViewHolderForAdapterPosition(nextPos)
                            ?.itemView
                            ?.findViewById<EditText>(R.id.etPersonName)
                            ?.requestFocus()
                    }

                } else {
                    // Last item → hide keyboard
                    v.clearFocus()
                    val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }

                true
            } else {
                false
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

       /* if (currentPosition != RecyclerView.NO_POSITION) {
            holder.btnAdd.visibility =
                if (currentPosition == list.size - 1) View.VISIBLE else View.GONE
        } else {
            holder.btnAdd.visibility = View.GONE
        }
        holder.btnRemove.visibility =
            if (list.size == 1) View.GONE else View.VISIBLE*/

        holder.btnAdd.visibility =
            if (position == list.size - 1) View.VISIBLE else View.GONE

        // ✅ Hide remove if only 1 item
        holder.btnRemove.visibility =
            if (list.size == 1) View.GONE else View.VISIBLE
    }
    fun updateList(newList: List<PersonModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}