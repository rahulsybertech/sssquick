package com.ssspvtltd.quick_app.ui.checkincheckout.activity

import android.os.Bundle
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.BaseViewModel
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityCheckInCheckOutBinding
import com.ssspvtltd.quick_app.ui.checkincheckout.fragment.CheckInCheckOutFragment
import com.ssspvtltd.quick_app.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckInCheckOutActivity :
    BaseActivity<ActivityCheckInCheckOutBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityCheckInCheckOutBinding> get() = ActivityCheckInCheckOutBinding::inflate
    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(CheckInCheckOutFragment(),binding.fragmentContainer.id,false)
    }
}