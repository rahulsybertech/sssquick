package com.ssspvtltd.quick_app.ui.order.pendinglr.activity

import android.os.Bundle
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.BaseViewModel
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityPendingLrBinding
import com.ssspvtltd.quick_app.ui.order.pendinglr.fragment.PendingLrFragment
import com.ssspvtltd.quick_app.utils.extension.getViewModel
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