package com.ssspvtltd.quick.ui.order.goodsreturn.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityGoodsReturnBinding
import com.ssspvtltd.quick.ui.checkincheckout.fragment.CheckInCheckOutFragment
import com.ssspvtltd.quick.ui.order.goodsreturn.fragment.GoodsReturnFragment
import com.ssspvtltd.quick.utils.extension.getViewModel
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