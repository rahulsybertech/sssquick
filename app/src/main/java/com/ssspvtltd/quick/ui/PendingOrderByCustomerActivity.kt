package com.ssspvtltd.quick.ui

import android.os.Bundle
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityCreateGrBinding
import com.ssspvtltd.quick.ui.create_gr.CreateGRFragment
import com.ssspvtltd.quick.ui.order.pending.fragment.PendingOrderByCustomer
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingOrderByCustomerActivity :   BaseActivity<ActivityCreateGrBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityCreateGrBinding> get() = ActivityCreateGrBinding::inflate
    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(PendingOrderByCustomer(),binding.fragmentContainer.id,false)
    }
}