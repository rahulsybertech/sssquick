package com.ssspvtltd.quick.ui.mailboxremark

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateA

import com.ssspvtltd.quick.databinding.ActivityGoodsReturnBinding

import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MailBoxRemarkActivity
    : BaseActivity<ActivityGoodsReturnBinding,BaseViewModel>() {
    override val inflate: InflateA<ActivityGoodsReturnBinding>
        get() = ActivityGoodsReturnBinding ::inflate

    override fun initViewModel(): BaseViewModel  = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragment(MailBoxRemarkFragment(),binding.fragmentContainer.id,false)
        //   addFragment(GoodsReturnFragmentNew(),binding.fragmentContainer.id,false)
    }
}