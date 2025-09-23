package com.ssspvtltd.quick.ui

import android.os.Bundle
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityCreateGrBinding
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.ui.order.add.fragment.AddOrderFragment
import com.ssspvtltd.quick.ui.order.pending.fragment.PendingOrderByCustomer
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrderActctivity : BaseActivity<ActivityCreateGrBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivityCreateGrBinding> get() = ActivityCreateGrBinding::inflate
    override fun initViewModel(): BaseViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = AddOrderFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PENDING_ORDER_ID, intent.getStringExtra(ARG_PENDING_ORDER_ID))
            }
        }

        addFragment(fragment, binding.fragmentContainer.id, false)
    }
}