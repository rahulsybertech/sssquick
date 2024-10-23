package com.ssspvtltd.quick_new.ui.order.pendinglr.activity

import android.os.Bundle
import com.ssspvtltd.quick_new.base.BaseActivity
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateA
import com.ssspvtltd.quick_new.databinding.ActivityPendingLrBinding
import com.ssspvtltd.quick_new.ui.order.pendinglr.fragment.PendingLrFragment
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingLrActivity : BaseActivity<ActivityPendingLrBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityPendingLrBinding>
        get() = ActivityPendingLrBinding::inflate

    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(PendingLrFragment(),binding.fragmentContainer.id,false)

    }
}