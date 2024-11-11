package com.ssspvtltd.quick_app.ui.order.add.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.databinding.ItemPackDataInputBinding
import com.ssspvtltd.quick_app.model.order.add.ItemsData
import com.ssspvtltd.quick_app.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick_app.utils.showToast
import tech.developingdeveloper.toaster.Toaster


class PackDataInputAdapter(val mContext: Context)  : RecyclerView.Adapter<PackDataInputAdapter.PackDataInputViewHolder>() {
    private val list = mutableListOf(PackTypeItem("", "", ""))
    private val itemSuggestions = mutableListOf<ItemsData>()
    private lateinit var myBinding: ItemPackDataInputBinding


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PackDataInputViewHolder {
        val binding = ItemPackDataInputBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PackDataInputViewHolder(binding).apply {
            this.binding.btnAddDelete.setOnClickListener {
                if (list.getOrNull(bindingAdapterPosition)?.itemID.isNullOrBlank()) {
                    this.binding.etItem.text.clear()
                }
                if (bindingAdapterPosition == list.lastIndex) {
                   if( validator(this.binding)) {
                        addNewItem()
                    }
                }
                else if (bindingAdapterPosition != -1) {
                    deleteItem(bindingAdapterPosition)
                }
            }
            this.binding.etItem.setOnItemClickListener { parent, view, position, id ->
                val suggestion = parent.getItemAtPosition(position) as? ItemsData

                binding.tilItem.isErrorEnabled = false
                list[bindingAdapterPosition].itemID = suggestion?.itemID
                list[bindingAdapterPosition].itemName = suggestion?.itemName
            }
            this.binding.etItem.setOnFocusChangeListener { _, _ ->
                if (list.getOrNull(bindingAdapterPosition)?.itemID.isNullOrBlank()) {
                    this.binding.etItem.text.clear()
                }
            }
            this.binding.etQuantity.doOnTextChanged { text, _, _, _ ->
                if (bindingAdapterPosition == -1) return@doOnTextChanged
                list[bindingAdapterPosition].itemQuantity = text?.toString()
                binding.tilQuantity.isErrorEnabled = false
            }
        }
    }

    override fun onBindViewHolder(holder: PackDataInputViewHolder, position: Int) {
        holder.bind(list[position])
        myBinding = holder.binding

        if (position == list.lastIndex) {
            myBinding.etQuantity.isEnabled              = true
            myBinding.tilQuantity.boxBackgroundColor    = getColor(mContext, R.color.white)

            myBinding.etItem.isEnabled              = true
            myBinding.tilItem.boxBackgroundColor    = getColor(mContext, R.color.white)
        } else {
            myBinding.etQuantity.isEnabled              = false
            myBinding.tilQuantity.boxBackgroundColor    = getColor(mContext, R.color.light_gray)

            myBinding.etItem.isEnabled              = false
            myBinding.tilItem.boxBackgroundColor    = getColor(mContext, R.color.light_gray)
        }


        holder.binding.etItem.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!itemSuggestions.any { it-> it.itemName == holder.binding.etItem.text.toString() }) {
                    holder.binding.etItem.text.clear()
                }
            }
            if (!holder.binding.etItem.isPopupShowing) {
                holder.binding.etItem.showDropDown()
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i("TaG","item list data -=-=-=-=-=->>>> $list")
        return list.size
    }

    private fun validator(binding: ItemPackDataInputBinding): Boolean {
       if(binding.etItem.text.isNullOrEmpty()){
           binding.tilItem.isErrorEnabled = true
           binding.tilItem.error = "select item"
           return false
       } else if(binding.etQuantity.text.isNullOrEmpty()){
           binding.tilQuantity.isErrorEnabled = true
           binding.tilQuantity.error = "select qty."
           return false
       }

        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<PackTypeItem>) {
        this.list.clear()
        if (list.isEmpty()) this.list.add(PackTypeItem("", "", ""))
        else list.forEach { this.list.add(it.copy()) }
        notifyDataSetChanged()
    }

    private fun addNewItem() {
        if (this.list.size >= 5) {
            showToast("You cannot add more than 5 items", Toaster.LENGTH_SHORT)
        } else {
            this.list.add(PackTypeItem("", "", ""))
            // notifyItemInserted(this.list.lastIndex)
            // notifyItemChanged(this.list.lastIndex - 1)
            notifyDataSetChanged()
        }
    }

    private fun deleteItem(position: Int) {
        this.list.removeAt(position)
        // notifyItemRemoved(position)
        notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSuggestions(itemSuggestions: List<ItemsData>) {
        this.itemSuggestions.clear()
        this.itemSuggestions.addAll(itemSuggestions)
        notifyDataSetChanged()
    }

    fun getList(): List<PackTypeItem> = list

    inner class PackDataInputViewHolder(val binding: ItemPackDataInputBinding) :
        BaseViewHolder(binding) {
        fun bind(item: PackTypeItem) = with(binding) {
            btnAddDelete.setImageResource(
                if (bindingAdapterPosition == list.lastIndex) R.drawable.add_circle_fill
                else R.drawable.ic_baseline_delete_24
            )

            if (list.size >= 1) {
                if(bindingAdapterPosition == 0){
                    tilItem.hint        = getString(R.string.item_ast)
                    tilQuantity.hint    = getString(R.string.quantity_ast)
                } else {
                    tilItem.hint        = getString(R.string.item)
                    tilQuantity.hint    = getString(R.string.quantity)
                }
            } else {
                tilItem.hint        = getString(R.string.item)
                tilQuantity.hint    = getString(R.string.quantity)
            }


            if (bindingAdapterPosition != list.lastIndex) {
                etQuantity.isEnabled = false
                tilQuantity.boxBackgroundColor = getColor(R.color.light_gray)

                etItem.isEnabled = false
                tilItem.boxBackgroundColor = getColor(R.color.light_gray)
            }

            etQuantity.setText(item.itemQuantity.orEmpty())
            etItem.setText(item.itemName.orEmpty())
            etItem.threshold = 1
            etItem.setAdapter(
                ItemDataListAdapter(itemView.context, R.layout.item_saleparty, itemSuggestions)
            )
            etItem.dropDownWidth = 900
            binding.etItem.setOnFocusChangeListener { v, hasFocus ->
                if (!binding.etItem.isPopupShowing) {
                    binding.etItem.showDropDown()
                }
            }
            binding.etItem.setOnClickListener {
                if (!binding.etItem.isPopupShowing) {
                    binding.etItem.showDropDown()
                }
            }
        }
    }
}