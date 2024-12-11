package com.ssspvtltd.quick.ui.order.pending.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentPendingOrderBinding
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderAdapter
import com.ssspvtltd.quick.ui.order.pending.viewmodel.PendingOrderViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PendingOrderFragment : BaseFragment<FragmentPendingOrderBinding, PendingOrderViewModel>() {
    private val mAdapter by lazy { PendingOrderAdapter() }
    override val inflate: InflateF<FragmentPendingOrderBinding>
        get() = FragmentPendingOrderBinding::inflate

    override fun initViewModel(): PendingOrderViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callPendingOrderApi()
        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        registerListener()
    }

    private fun callPendingOrderApi() {
        val filterRequest = FilterRequest(null, null, null, null, true)
        viewModel.getPendingOrder(filterRequest)
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {

            mAdapter.submitList(viewModel.widgetList)
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        toolbar.setTitle("Pending Order")
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredList()
        }.launchIn(lifecycleScope)
        // mAdapter.onEditClick ={
        //     addFragment(AddOrderFragment(),R.id.addOrderFragment,false)
        // }
        mAdapter.onItemClick = {
            println("GET_DATA_ONCLICK ${it.orderID}")
            PendingOrderDetailsBottomSheetFragment.newInstance(it).show(
                childFragmentManager, PendingOrderDetailsBottomSheetFragment::class.simpleName
            )
        }
        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.deep_orange_800))
        swipeRefreshLayout.setOnRefreshListener {
            callPendingOrderApi()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}