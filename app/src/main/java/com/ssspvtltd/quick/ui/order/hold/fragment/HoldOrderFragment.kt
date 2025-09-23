package com.ssspvtltd.quick.ui.order.hold.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentHoldOrderBinding
import com.ssspvtltd.quick.ui.order.hold.holdinterface.VisibilityListener
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderAdapter
import com.ssspvtltd.quick.ui.order.hold.viewmodel.HoldOrderViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HoldOrderFragment : BaseFragment<FragmentHoldOrderBinding, HoldOrderViewModel>(),
    VisibilityListener {

    private val mAdapter by lazy { HoldOrderAdapter() }
    override val inflate: InflateF<FragmentHoldOrderBinding>
        get() = FragmentHoldOrderBinding::inflate

    var holdOrderDetailsBottomSheetFragment : HoldOrderDetailsBottomSheetFragment?= null

    override fun initViewModel(): HoldOrderViewModel = getViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getHoldOrder()
        registerObserver()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getHoldOrder()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            setTitle("Hold Order")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }


        initViews()
        registerListener()
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            if (viewModel.widgetList.isEmpty()){
                binding.noData.visibility = View.VISIBLE
                binding.recyclerView.visibility=View.GONE
            }else{
                binding.noData.visibility = View.GONE
                binding.recyclerView.visibility=View.VISIBLE
            }
            mAdapter.submitList(viewModel.widgetList)
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        toolbar.setTitle("Hold Order")
    }

    private fun registerListener() = with(binding) {

        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredList()
        }.launchIn(lifecycleScope)
        mAdapter.onItemClick = {

            holdOrderDetailsBottomSheetFragment = HoldOrderDetailsBottomSheetFragment(it)
            holdOrderDetailsBottomSheetFragment!!.visibilityListener = this@HoldOrderFragment
//            holdOrderDetailsBottomSheetFragment.newInstance(it)
            holdOrderDetailsBottomSheetFragment!!.show(childFragmentManager, HoldOrderDetailsBottomSheetFragment::class.simpleName)
        }
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.deep_orange_800))
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getHoldOrder()

            swipeRefreshLayout.isRefreshing = false

        }

    }

    override fun onVisibilityChanged(isVisible: Boolean) {
        println("ParentFragment_BottomSheet_is_visible: $isVisible")
        if (!isVisible) {
            if (holdOrderDetailsBottomSheetFragment != null) holdOrderDetailsBottomSheetFragment!!.dismiss()
            viewModel.getHoldOrder()
        }
    }
}