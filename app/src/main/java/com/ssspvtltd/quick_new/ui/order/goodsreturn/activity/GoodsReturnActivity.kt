package com.ssspvtltd.quick_new.ui.order.goodsreturn.activity

import android.os.Bundle
import com.ssspvtltd.quick_new.base.BaseActivity
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateA
import com.ssspvtltd.quick_new.databinding.ActivityGoodsReturnBinding
import com.ssspvtltd.quick_new.ui.order.goodsreturn.fragment.GoodsReturnFragment
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoodsReturnActivity : BaseActivity<ActivityGoodsReturnBinding,BaseViewModel>() {
    override val inflate: InflateA<ActivityGoodsReturnBinding>
        get() = ActivityGoodsReturnBinding ::inflate

    override fun initViewModel(): BaseViewModel  = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(GoodsReturnFragment(),binding.fragmentContainer.id,false)
    }
}