package com.ssspvtltd.quick_new.ui.order.stockinoffice.activity

import android.os.Bundle
import com.ssspvtltd.quick_new.base.BaseActivity
import com.ssspvtltd.quick_new.base.InflateA
import com.ssspvtltd.quick_new.databinding.ActivityStockInOfficeBinding
import com.ssspvtltd.quick_new.ui.order.stockinoffice.fragment.StockInOfficeFragment
import com.ssspvtltd.quick_new.ui.order.stockinoffice.viewmodel.StockInOfficeViewModel
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockInOfficeActivity : BaseActivity<ActivityStockInOfficeBinding,StockInOfficeViewModel>() {
    override val inflate: InflateA<ActivityStockInOfficeBinding>
        get() = ActivityStockInOfficeBinding :: inflate

    override fun initViewModel(): StockInOfficeViewModel =  getViewModel()

    override fun onCreate(savedInstanceState: Bundle?  )   {
        super.onCreate(savedInstanceState)
        addFragment(StockInOfficeFragment(),binding.fragmentContainer.id,false)
    }
}