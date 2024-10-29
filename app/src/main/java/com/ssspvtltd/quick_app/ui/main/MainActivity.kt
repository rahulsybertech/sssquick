package com.ssspvtltd.quick_app.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityMainBinding
import com.ssspvtltd.quick_app.di.PrefHelperEntryPoint
import com.ssspvtltd.quick_app.ui.auth.activity.LoginActivity
import com.ssspvtltd.quick_app.ui.auth.viewmodel.LoginViewModel
import com.ssspvtltd.quick_app.ui.checkincheckout.activity.CheckInCheckOutActivity
import com.ssspvtltd.quick_app.ui.order.goodsreturn.activity.GoodsReturnActivity
import com.ssspvtltd.quick_app.ui.order.pendinglr.activity.PendingLrActivity
import com.ssspvtltd.quick_app.ui.order.stockinoffice.activity.StockInOfficeActivity
import com.ssspvtltd.quick_app.utils.extension.getViewModel
import com.ssspvtltd.quick_app.utils.extension.observe
import com.ssspvtltd.quick_app.utils.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, LoginViewModel>() {

    private lateinit var navController: NavController
    override val inflate: InflateA<ActivityMainBinding> get() = ActivityMainBinding::inflate
    override fun initViewModel(): LoginViewModel = getViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.autoLogout()


        registerObserver()
        registerListner()
    }

    override fun onResume() {
        super.onResume()
        getTest()
    }

    fun getTest() {

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isComeFromCheckIn = sharedPreferences.getBoolean("isComeFromCheckIn", false)
        if (isComeFromCheckIn) {
            binding.bottomNavView.selectedItemId = R.id.addOrderFragment
            sharedPreferences.edit().remove("isComeFromCheckIn").apply()
        }

    }

    private fun registerListner() {
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
        lifecycleScope.launch {
            mobileNo.setText(viewModel.prefHelper.getMarketerMobile())
            marketerCode.setText(viewModel.prefHelper.getMarketerCode ())
            marketerName.setText(viewModel.prefHelper.getUserName())
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

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }


    fun setupWithNavController(
        navigationBarView: NavigationBarView,
        navController: NavController
    ) {
        navigationBarView.setOnItemSelectedListener { item ->//2131296328
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
                //viewModel.showMsgAlert(message = "Checkin First")
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

}

//    private val drawerAction: (DrawerAction) -> Unit = {
//        when (it) {
//            DrawerAction.DASHBOARD -> {
//                closeDrawer()
//                if (navController.currentDestination?.id != R.id.dashboardFragment) {
//                    navController.navigate(R.id.dashboardFragment)
//                }
//            }
//
//            DrawerAction.PENDINGORDERS -> {
//                closeDrawer()
//                if (navController.currentDestination?.id != R.id.pendingorderFragment) {
//                    navController.navigate(R.id.pendingorderFragment)
//                }
//            }
//
//            DrawerAction.ADDORDER -> {
//                closeDrawer()
//                if (navController.currentDestination?.id != R.id.addorderFragment) {
//                    navController.navigate(R.id.addorderFragment)
//                }
//            }
//
//            DrawerAction.PENDINGLR -> {
//                closeDrawer()
////                startActivity(Intent(this, PostListActivity::class.java))
//            }
//
//            DrawerAction.STOCKINOFFICE -> {
//                closeDrawer()
////                startActivity(Intent(this, NotificationActivity::class.java))
//            }
//
//            DrawerAction.HOLDORDERS -> {
//                closeDrawer()
//            }
//
//            DrawerAction.GOODRETURN -> {
//                closeDrawer()
//            }
//
//            DrawerAction.PENDINGLR -> {
//                closeDrawer()
//            }
//
//            DrawerAction.SHARE -> {
//                val shareMessage = "SSS QUICK \nDownload the application\n\n"
//                ShareCompat.IntentBuilder(this)
//                    .setType("text/plain")
//                    .setChooserTitle("Share SSS QUICK App")
//                    .setSubject("SSS QUICK")
//                    .setText("${shareMessage}https://play.google.com/store/apps/details?id=${this.packageName}")
//                    .startChooser()
//            }
//
//            DrawerAction.LOGOUT -> {
//                closeDrawer()
////                showWarningDialog(
////                    getString(R.string.logout_title), getString(R.string.logout_msg),
////                    getString(R.string.logout_positive_btn), getString(R.string.logout_negative_btn)
////                ) {
////                    mViewModel.logout()
////                    it.dismissWithAnimation()
////                    val intent = Intent(this, LoginActivity::class.java)
////                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////                    startActivity(intent)
////                }
//            }
//
//            else -> {}
//        }
//    }

//    fun registerObserver() {
//        mViewModel.getListDataAvailable().observe(this) {
//            mAdapter.submitList(mViewModel.getList(), it)
//        }
//    }
