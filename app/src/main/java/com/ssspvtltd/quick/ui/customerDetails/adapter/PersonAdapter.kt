package com.ssspvtltd.quick.ui.customerDetails.adapter

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
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
    private val onMobileClick: (position: Int, mobile: String) -> Unit,
    private val zoomIN: (position: Int, mobile: String,type:String) -> Unit,
    var onRequestNextFocus: ((Int) -> Unit)? = null,
) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    private var focusNamePosition = -1
    private var focusMobilePosition = -1
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val etName: EditText = itemView.findViewById(R.id.etPersonName)
        val zoomIN: RelativeLayout = itemView.findViewById(R.id.zoomIN)
        val zoonInOutBack: RelativeLayout = itemView.findViewById(R.id.zoonInOutBack)
        val etPMobileNumber: EditText = itemView.findViewById(R.id.etPMobileNumber)
        val etPersonRemark: EditText = itemView.findViewById(R.id.etPersonRemark)
        val imgPhoneBook: ImageView = itemView.findViewById(R.id.imgPhoneBook)
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
        holder.etPersonRemark.setText(item.personRemark ?: "")
        holder.etPMobileNumber.setText(item.mobileNo ?: "")



        // In onBindViewHolder
        holder.imgPhoneBook.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onMobileClick(pos, holder.etPMobileNumber.text.toString())
            }
        }
        holder.zoomIN.setOnClickListener {
            val pos = holder.bindingAdapterPosition

            when {

                !item.aadharFrontBase64.isNullOrEmpty() -> {

                    zoomIN(
                        pos,
                        item.aadharFrontBase64!!,
                        "noturl"
                    )
                }

                !item.frontURL.isNullOrEmpty() -> {

                    zoomIN(
                        pos,
                        item.frontURL,
                        "url"
                    )
                }

                else -> {

                    zoomIN(pos, "", "image not found")
                }
            }
        }

        holder.zoonInOutBack.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            when {

                !item.aadharBackBase64.isNullOrEmpty() -> {

                    zoomIN(pos, item.aadharBackBase64!!, "noturl")
                }

                !item.backURL.isNullOrEmpty() -> {

                    zoomIN(pos, item.backURL, "url")
                }

                else -> {

                    zoomIN(pos, "", "")
                }
            }
        }


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

        val watcherPersonRemark = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val text = s.toString()

                // 🚫 Prevent space at start
                if (text.startsWith(" ")) {
                    val trimmed = text.trimStart()
                    holder.etPersonRemark.setText(trimmed)
                    holder.etPersonRemark.setSelection(trimmed.length)
                    return
                }

                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    list[pos].personRemark = text
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        val watcher1 = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                val text = s.toString()

                // 🚫 Prevent space at start
                if (text.startsWith(" ")) {
                    val trimmed = text.trimStart()
                    holder.etPMobileNumber.setText(trimmed)
                    holder.etPMobileNumber.setSelection(trimmed.length)
                    return
                }

                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    list[pos].mobileNo = text
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }


        holder.etName.addTextChangedListener(watcher)
        holder.etPersonRemark.addTextChangedListener(watcherPersonRemark)
        holder.etPMobileNumber.addTextChangedListener(watcher1)

        // 🔥 Save watcher in tag
        holder.etName.tag = watcher
        holder.etPersonRemark.tag = watcherPersonRemark

        if (item.aadharFrontBitmap != null) {

            Glide.with(holder.itemView.context)
                .load(item.aadharFrontBitmap)
                .placeholder(R.drawable.empty_photo)
                .into(holder.imgFront)

        } else if (item.frontURL.isNotEmpty()) {

            Glide.with(holder.itemView.context)
                .load(item.frontURL)
                .placeholder(R.drawable.empty_photo)
                .error(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgFront)

        } else {

            holder.imgFront.setImageResource(R.drawable.empty_photo)
        }
        if (item.aadharBackBitmap != null) {

            Glide.with(holder.itemView.context)
                .load(item.aadharBackBitmap)
                .placeholder(R.drawable.empty_photo)
                .into(holder.imgBack)

        } else if (item.backURL.isNotEmpty()) {

            Glide.with(holder.itemView.context)
                .load(item.backURL)
                .placeholder(R.drawable.empty_photo)
                .error(R.drawable.empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgBack)

        } else {

            holder.imgBack.setImageResource(R.drawable.empty_photo)
        }

        holder.btnAdd.setOnClickListener {
            if (item.personName.isEmpty()) {
                showToast("Please Enter Person Name")
            }  else if (item.mobileNo.isEmpty()) {
                showToast("Enter valid mobile number")
            }
            else if (item.mobileNo.isNotEmpty() && !item.mobileNo.matches(Regex("^[6-9][0-9]{9}$"))) {
                showToast("Enter valid mobile number")
        }
            else if (item.frontURL.isNotEmpty() || item.aadharFrontBase64?.isNotEmpty() == true||item.backURL.isNotEmpty() || item.aadharBackBase64?.isNotEmpty() == true) {
                onAddClick(position)
                onRequestNextFocus?.invoke(position + 1)
            }
            else {
                showToast("At Least one Aadhar photo is required.")
            }
        }
        holder.etPMobileNumber.filters = arrayOf(
            InputFilter.LengthFilter(10)
        )

        holder.etPMobileNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {

                val input = s.toString()

                if (input.startsWith("91") && input.length > 10) {

                    holder.etPMobileNumber.setText(
                        input.removePrefix("91")
                    )

                    holder.etPMobileNumber.setSelection(
                        holder.etPMobileNumber.text.length
                    )
                }
            }
        })
        holder.etName.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_DONE) {

                holder.etPMobileNumber.requestFocus()

                holder.etPMobileNumber.post {

                    holder.etPMobileNumber.setSelection(
                        holder.etPMobileNumber.text.length
                    )

                    val imm = holder.itemView.context
                        .getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager

                    imm.showSoftInput(
                        holder.etPMobileNumber,
                        InputMethodManager.SHOW_IMPLICIT
                    )
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



        // Focus Name
        if (focusNamePosition == position) {

            holder.etName.requestFocus()

            holder.etName.post {

                holder.etName.setSelection(
                    holder.etName.text.length
                )

                val imm = holder.itemView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager

                imm.showSoftInput(
                    holder.etName,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }

            focusNamePosition = -1
        }

// Focus Mobile
        if (focusMobilePosition == position) {

            holder.etPMobileNumber.requestFocus()

            holder.etPMobileNumber.post {

                holder.etPMobileNumber.setSelection(
                    holder.etPMobileNumber.text.length
                )

                val imm = holder.itemView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager

                imm.showSoftInput(
                    holder.etPMobileNumber,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }

            focusMobilePosition = -1
        }
    }
    fun updateList(newList: List<PersonModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    fun requestNameFocus(position: Int) {

        focusNamePosition = position

        notifyItemChanged(position)
    }

    fun requestMobileFocus(position: Int) {

        focusMobilePosition = position

        notifyItemChanged(position)
    }
}