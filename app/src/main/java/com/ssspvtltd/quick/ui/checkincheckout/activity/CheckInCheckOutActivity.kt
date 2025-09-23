package com.ssspvtltd.quick.ui.checkincheckout.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityCheckInCheckOutBinding
import com.ssspvtltd.quick.ui.checkincheckout.fragment.CheckInCheckOutFragment
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckInCheckOutActivity :
    BaseActivity<ActivityCheckInCheckOutBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityCheckInCheckOutBinding> get() = ActivityCheckInCheckOutBinding::inflate
    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(CheckInCheckOutFragment(),binding.fragmentContainer.id,false)
        // ✅ Change status bar color
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.deep_orange_800)

        // ✅ Control icon colors
        WindowInsetsControllerCompat(
            window,
           window.decorView
        ).isAppearanceLightStatusBars = false // false = white icons, true = dark icons

    }
}