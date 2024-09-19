package com.ssspvtltd.quick.ui.order.add.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.databinding.ItemPackDataInputBinding
import com.ssspvtltd.quick.model.order.add.ItemsData
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick.utils.showToast
import tech.developingdeveloper.toaster.Toaster


class PackDataInputAdapter : RecyclerView.Adapter<PackDataInputAdapter.PackDataInputViewHolder>() {
    private val list = mutableListOf(PackTypeItem("", "", ""))
    private val itemSuggestions = mutableListOf<ItemsData>()


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): PackDataInputViewHolder {
        val binding = ItemPackDataInputBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PackDataInputViewHolder(binding).apply {
            this.binding.btnAddDelete.setOnClickListener {
                if (bindingAdapterPosition == list.lastIndex) addNewItem()
                else if (bindingAdapterPosition != -1) deleteItem(bindingAdapterPosition)
            }
            this.binding.etItem.setOnItemClickListener { parent, view, position, id ->
                val suggestion = parent.getItemAtPosition(position) as? ItemsData
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
            }
        }
    }

    override fun onBindViewHolder(holder: PackDataInputViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<PackTypeItem>) {
        this.list.clear()
        if (list.isEmpty()) this.list.add(PackTypeItem("", "", ""))
        else list.forEach { this.list.add(it.copy()) }
        notifyDataSetChanged()
    }

    private fun addNewItem() {
        if (this.list.size >= 50) {
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
                if (bindingAdapterPosition == list.lastIndex) R.drawable.ic_baseline_plus_24
                else R.drawable.ic_baseline_delete_24
            )
            etQuantity.setText(item.itemQuantity.orEmpty())
            etItem.setText(item.itemName.orEmpty())
            etItem.threshold = 1
            etItem.setAdapter(
                ItemDataListAdapter(itemView.context, R.layout.item_saleparty, itemSuggestions)
            )
        }
    }
}