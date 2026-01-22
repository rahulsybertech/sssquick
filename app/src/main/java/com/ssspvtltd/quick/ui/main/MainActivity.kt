package com.ssspvtltd.quick.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.FloatingWindow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.navigation.NavigationBarView
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityMainBinding
import com.ssspvtltd.quick.di.PrefHelperEntryPoint
import com.ssspvtltd.quick.ui.PendingOrderByCustomerActivity
import com.ssspvtltd.quick.ui.auth.activity.LoginActivity
import com.ssspvtltd.quick.ui.auth.viewmodel.LoginViewModel
import com.ssspvtltd.quick.ui.checkincheckout.activity.CheckInCheckOutActivity
import com.ssspvtltd.quick.ui.create_gr.CreateGRActivity
import com.ssspvtltd.quick.ui.create_gr.CreateGRFragment
import com.ssspvtltd.quick.ui.mailboxremark.MailBoxRemarkActivity
import com.ssspvtltd.quick.ui.order.goodsreturn.activity.GoodsReturnActivity
import com.ssspvtltd.quick.ui.order.pendinglr.activity.PendingLrActivity
import com.ssspvtltd.quick.ui.order.stockinoffice.activity.StockInOfficeActivity
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.observe
import com.ssspvtltd.quick.utils.showWarningDialog
import com.ssspvtltd.quick.utils.versionName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.time.Year


@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, LoginViewModel>() {

    private lateinit var navController: NavController
    override val inflate: InflateA<ActivityMainBinding> get() = ActivityMainBinding::inflate
    override fun initViewModel(): LoginViewModel = getViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.autoLogout()

        viewModel.getCheckInStatus()

        registerObserver()
        registerListener()
    }



    override fun onResume() {
        super.onResume()
        getTest()
    }

    private fun getTest() {

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isComeFromCheckIn = sharedPreferences.getBoolean("isComeFromCheckIn", false)
        if (isComeFromCheckIn) {
            binding.bottomNavView.selectedItemId = R.id.addOrderFragment
            sharedPreferences.edit().remove("isComeFromCheckIn").apply()
        }

    }

    private fun registerListener() {
        Log.e(
            "getCheckinStatus",
            (runBlocking { viewModel.prefHelper.getCheckinStatus() }).toString()
        )
        Log.e("getAccessToken", runBlocking { PrefHelperEntryPoint.prefHelper.getAccessToken() } ?: "")

        viewModel.prefHelper.getUserNameAsFlow().observe(this) {
            Log.e("getUserNameAsFlow", it.orEmpty())
        }
        lifecycleScope.launch {
            Log.e("userName", viewModel.prefHelper.getUserName() ?: "")
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(R.navigation.main_nav_graph)
        setupWithNavController(binding.bottomNavView, navController)
        // binding.bottomNavView.setupWithNavController(cnavController)
        binding.bottomNavView.setOnItemReselectedListener { }

//        mAdapter = MainDrawerAdapter(drawerAction)
//        binding.drawerRecycler.adapter = mAdapter
        binding.navView.setupWithNavController(navController)
        val headerView = binding.navView.getHeaderView(0)
        val marketerName: TextView = headerView.findViewById(R.id.marketer_name)
        val mobileNo: TextView = headerView.findViewById(R.id.marketer_mobile)
        val marketerCode: TextView = headerView.findViewById(R.id.marketer_code)
        val versionName : TextView = binding.navView.findViewById(R.id.verName)
        versionName.text = versionName()
        lifecycleScope.launch {
            mobileNo.text = viewModel.prefHelper.getMarketerMobile()
            marketerCode.text = viewModel.prefHelper.getMarketerCode ()
            marketerName.text = viewModel.prefHelper.getUserName()
        }




        binding.navView.setNavigationItemSelectedListener {
            closeDrawer()
            when (it.itemId) {

                R.id.nav_checkinout -> {
                    val intent = Intent(this, CheckInCheckOutActivity::class.java)
                    startActivity(intent)
                    false
                }

                // R.id.addOrderFragment -> {
                //     if ( runBlocking { viewModel.prefHelper.getCheckinStatus() } == true) {
                //         addFragment(PendingLrFragment(),binding.fragmentContainer.id,false)
                //     }else{
                //         showErrorDialog(message = "Checkin First")
                //     }
                //         false
                //
                // }

                R.id.nav_goods_return -> {
                    val intent = Intent(this, GoodsReturnActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.nav_pending_lr -> {
                    val intent = Intent(this, PendingLrActivity::class.java)
                    startActivity(intent)
                    false
                }

                R.id.nav_mail_box -> {
                    val intent = Intent(this, MailBoxRemarkActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.navCreateGR -> {

                    val intent = Intent(this, CreateGRActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.pendingOrderByCustomer2 -> {
                    val intent = Intent(this
                        , PendingOrderByCustomerActivity::class.java).apply {
                        putExtra("source", "Customer")
                    }
                    startActivity(intent)
            false
                 }
                R.id.pendingorderFragment -> {
                    val bundle = Bundle().apply {
                        putString("source", "Markerter")
                    }
                    navController.navigate(R.id.pendingorderFragment, bundle)
            false
                 }

                R.id.nav_stockin_office -> {
                    val intent = Intent(this, StockInOfficeActivity::class.java)
                    startActivity(intent)
                    false
                }
                // R.id.nav_stockin_office -> {
                //     val intent = Intent(this, StockInOfficeActivity::class.java)
                //     startActivity(intent)
                //     false
                // }

                R.id.nav_logout -> {
                    showWarningDialog(
                        getString(R.string.logout_title),
                        getString(R.string.logout_msg),
                        getString(R.string.logout_positive_btn),
                        getString(R.string.logout_negative_btn)
                    ) {
                        // logout()
                        viewModel.logoutApi()
                        it.dismissWithAnimation()
                    }
                    false
                }

                else -> {
                    it.onNavDestinationSelected(navController)
                    true
                }
            }
        }
    }

    private fun registerObserver() {
        viewModel.logoutData.observe(this) {
            if (it == null) return@observe
            // showToast(it.message.orEmpty())
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finishAffinity()
        }
        viewModel.loginStatus.observe(this){
            // showToast(it.toString())
            if (it==false){
                 runBlocking { viewModel.prefHelper.clearPref()}
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity()
            }
        }
    }


    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }


    private fun setupWithNavController(
        navigationBarView: NavigationBarView,
        navController: NavController
    ) {
        navigationBarView.setOnItemSelectedListener { item ->// 2131296328
            Log.i("TaG","my nav ItemId -=-=-=> ${item.itemId}")
            if (item.itemId != R.id.addOrderFragment || runBlocking { viewModel.prefHelper.getCheckinStatus() } == true) {
                NavigationUI.onNavDestinationSelected(item, navController, false)
            } else {
                val intent = Intent(this, CheckInCheckOutActivity::class.java)
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).apply {
                    titleText = ""
                    contentText = "You need to checkIn First."
                    setCancelable(false)
                    confirmText = "OK"
                    cancelText =  "Cancel"
                    confirmButtonBackgroundColor = getColor( R.color.error_text)
                    this.setConfirmClickListener {
                        dismiss()
                        startActivity(intent)
                    }
                }.show()
                // viewModel.showMsgAlert(message = "Checkin First")
                false
            }
        }
        val weakReference = WeakReference(navigationBarView)
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    if (destination is FloatingWindow) {
                        return
                    }
                    view.menu.forEach { item ->
                        if (destination.hierarchy.any { it.id == item.itemId }) {
                            item.isChecked = true
                        }
                    }
                }
            })
    }

    fun logout() {
        runBlocking { viewModel.prefHelper.logout() }
    }


   fun higherOrder(x:Int,y:Int,operation:(Int,Int)->Int):Int{
       return operation(x,y);
   }
    val sum = higherOrder(5, 3) { a, b -> a + b }

}

