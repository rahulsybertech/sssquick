 package com.ssspvtltd.quick_new.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick_new.base.BaseActivity
import com.ssspvtltd.quick_new.base.BaseViewModel
import com.ssspvtltd.quick_new.base.InflateA
import com.ssspvtltd.quick_new.databinding.ActivitySplashBinding
import com.ssspvtltd.quick_new.ui.auth.activity.LoginActivity
import com.ssspvtltd.quick_new.ui.main.MainActivity
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding, BaseViewModel>() {
    override val inflate: InflateA<ActivitySplashBinding> get() = ActivitySplashBinding::inflate
    override fun initViewModel(): BaseViewModel = getViewModel<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(1000)
            val intent = if (!viewModel.prefHelper.getAccessToken()     .isNullOrBlank()) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }

    }
}