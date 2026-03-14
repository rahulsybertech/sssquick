package com.ssspvtltd.quick.ui.customerDetails
import android.os.Bundle
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityCustomerDetailsBinding
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CustomerDetailsActivity : BaseActivity<ActivityCustomerDetailsBinding,BaseViewModel>() {
    override val inflate: InflateA<ActivityCustomerDetailsBinding>
        get() = ActivityCustomerDetailsBinding ::inflate

    override fun initViewModel(): BaseViewModel  = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(CustomerDetailsFragment(),binding.fragmentContainer.id,false)
        //   addFragment(GoodsReturnFragmentNew(),binding.fragmentContainer.id,false)
    }
}