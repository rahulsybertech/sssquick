package com.ssspvtltd.quick.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.recycler.adapter.BaseViewHolder
import com.ssspvtltd.quick.base.recycler.adapter.MultiViewAdapter
import com.ssspvtltd.quick.databinding.ItemDrawerBinding
import com.ssspvtltd.quick.databinding.RowCommonShimmerBinding
import com.ssspvtltd.quick.model.DrawerViewType
import com.ssspvtltd.quick.model.drawer.DrawerItem
import com.ssspvtltd.quick.utils.extension.dp

class  MainDrawerAdapter() : MultiViewAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            DrawerViewType.VIEW_TYPE_DRAWER_HEADER.id -> {
                val imageView = ImageView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                          RecyclerView.LayoutParams.MATCH_PARENT, 100.dp
                    )
                    setBackgroundColor(this.context.getColor(R.color.deep_orange_800))
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    adjustViewBounds  = true
                    setPadding(20.dp)
                }
                return DrawerHeaderVH(imageView)
            }

            DrawerViewType.VIEW_TYPE_DRAWER_ITEM.id -> {
                val binding = ItemDrawerBinding.inflate(inflater, parent, false)
                val holder = DrawerItemVH(binding)
                return holder
            }

            DrawerViewType.VIEW_TYPE_SMALL_DIVIDER.id -> {
                val view = View(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, 1.dp
                    )
                    setBackgroundResource(R.color.grey_200)
                }
                return DrawerDividerVH(view)
            }

            DrawerViewType.VIEW_TYPE_LARGE_DIVIDER.id -> {
                val view = View(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, 5.dp
                    )
                    setBackgroundResource(R.color.grey_200)
                }
                return DrawerDividerVH(view)
            }

            else -> {
                val binding = RowCommonShimmerBinding.inflate(inflater, parent, false)
                return ShimmerVH(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is DrawerHeaderVH -> {
                getItemOrNull<DrawerItem>(position)?.icon?.let(holder::bind)
            }

            is DrawerItemVH -> {
                getItemOrNull<DrawerItem>(position)?.let(holder::bind)
            }
        }
    }
}