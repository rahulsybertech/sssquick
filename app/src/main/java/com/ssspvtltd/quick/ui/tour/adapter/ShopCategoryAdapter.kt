package com.ssspvtltd.quick.ui.tour.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.databinding.ItemCategoryBinding
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryItem

class CategoryAdapter(
    private val list: MutableList<ShopCategoryItem>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var isCategoryEditable = false
    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.checkCategory.text = item.name

        holder.binding.checkCategory.setOnCheckedChangeListener(null)

        holder.binding.checkCategory.isChecked = item.isSelected
        holder.binding.checkCategory.isEnabled = isCategoryEditable
        holder.binding.checkCategory.setOnCheckedChangeListener { _, isChecked ->

            if (holder.binding.checkCategory.isEnabled) {
                item.isSelected = isChecked
            }
        }
    }

    // Select All
    fun selectAll() {

        list.forEach {
            it.isSelected = true
        }

        notifyDataSetChanged()
    }

    // Unselect All
    fun unSelectAll() {

        list.forEach {
            it.isSelected = false
        }

        notifyDataSetChanged()
    }

    // Selected List

    fun getSelectedCategories(): List<ShopCategoryItem> {

        return list.filter { it.isSelected }
    }
}