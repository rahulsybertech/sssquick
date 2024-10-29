package com.ssspvtltd.quick_app.ui.main.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.ssspvtltd.quick_app.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick_app.databinding.ItemDrawerBinding
import com.ssspvtltd.quick_app.databinding.RowCommonShimmerBinding
import com.ssspvtltd.quick_app.model.drawer.DrawerItem

class ShimmerVH(binding: RowCommonShimmerBinding) : BaseViewHolder(binding.root)

class DrawerItemVH(val binding: ItemDrawerBinding) : BaseViewHolder(binding) {
    fun bind(drawerItem: DrawerItem) {
        binding.title.setText(drawerItem.title)
        if (drawerItem.desc != null && drawerItem.desc > 0) {
            binding.desc.setText(drawerItem.desc)
            binding.desc.isVisible = true
        } else {
            binding.desc.isVisible = false
        }
        if (drawerItem.icon != null && drawerItem.icon > 0) {
            binding.icon.setImageResource(drawerItem.icon)
            binding.icon.isVisible = true
        } else {
            binding.icon.isVisible = false
        }
    }
}

class DrawerHeaderVH(val imageView: ImageView) : BaseViewHolder(imageView) {
    fun bind(icon: Int) {
        imageView.setImageResource(icon)

    }
}

class DrawerDividerVH(view: View) : BaseViewHolder(view)