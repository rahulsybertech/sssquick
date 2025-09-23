package com.ssspvtltd.quick.ui.order.goodsreturn.activity

import android.os.Bundle
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityGoodsReturnBinding
import com.ssspvtltd.quick.ui.order.goodsreturn.fragment.GoodsReturnFragment
import com.ssspvtltd.quick.ui.order.goodsreturn.fragment.GoodsReturnSecondaryFragment
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoodReturnSecondaryActivity: BaseActivity<ActivityGoodsReturnBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityGoodsReturnBinding>
        get() = ActivityGoodsReturnBinding ::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra("ID") // Or getIntExtra() if it's an Int

        val fragment = GoodsReturnSecondaryFragment.newInstance(id)

        addFragment(fragment, binding.fragmentContainer.id, false)
        //   addFragment(GoodsReturnFragmentNew(),binding.fragmentContainer.id,false)
    }
}