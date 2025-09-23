package com.ssspvtltd.quick.ui.order.pendinglr.activity

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.WindowInsetsControllerCompat
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityPendingLrBinding
import com.ssspvtltd.quick.ui.order.pendinglr.fragment.PendingLrFragment
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingLrActivity : BaseActivity<ActivityPendingLrBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityPendingLrBinding>
        get() = ActivityPendingLrBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  /*      window.statusBarColor = Color.parseColor("#BF4625") // your app bar color (orange/brown)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false*/
        addFragment(PendingLrFragment(),binding.fragmentContainer.id,false)

    }
}