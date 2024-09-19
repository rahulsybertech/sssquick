package com.ssspvtltd.quick.ui.order.hold.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentHoldOrderBinding
import com.ssspvtltd.quick.databinding.FragmentPendingOrderBinding
import com.ssspvtltd.quick.ui.order.hold.adapter.HoldOrderAdapter
import com.ssspvtltd.quick.ui.order.hold.viewmodel.HoldOrderViewModel
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderAdapter
import com.ssspvtltd.quick.ui.order.pending.fragment.PendingOrderDetailsBottomSheetFragment
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HoldOrderFragment : BaseFragment<FragmentHoldOrderBinding,HoldOrderViewModel>() {

    private val mAdapter by lazy { HoldOrderAdapter() }
    override val inflate: InflateF<FragmentHoldOrderBinding>
        get() = FragmentHoldOrderBinding::inflate

    override fun initViewModel(): HoldOrderViewModel = getViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getHoldOrder()
        registerObserver()

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
            HoldOrderDetailsBottomSheetFragment.newInstance(it)
                .show(childFragmentManager, HoldOrderDetailsBottomSheetFragment::class.simpleName)

        }
    }

}