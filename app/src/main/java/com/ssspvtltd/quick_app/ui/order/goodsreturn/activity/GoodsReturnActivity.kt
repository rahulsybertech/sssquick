package com.ssspvtltd.quick_app.ui.order.goodsreturn.activity

import android.os.Bundle
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.BaseViewModel
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityGoodsReturnBinding
import com.ssspvtltd.quick_app.ui.order.goodsreturn.fragment.GoodsReturnFragment
import com.ssspvtltd.quick_app.utils.extension.getViewModel
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