package com.ssspvtltd.quick_app.ui.order.stockinoffice.activity

import android.os.Bundle
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityStockInOfficeBinding
import com.ssspvtltd.quick_app.ui.order.stockinoffice.fragment.StockInOfficeFragment
import com.ssspvtltd.quick_app.ui.order.stockinoffice.viewmodel.StockInOfficeViewModel
import com.ssspvtltd.quick_app.utils.extension.getViewModel
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