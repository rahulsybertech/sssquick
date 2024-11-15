package com.ssspvtltd.quick.ui.order.pendinglr.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentPendingLrBinding
import com.ssspvtltd.quick.ui.order.pendinglr.adapter.PendingLrAdapter
import com.ssspvtltd.quick.ui.order.pendinglr.viewmodel.PendingLrViewmodel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PendingLrFragment : BaseFragment<FragmentPendingLrBinding, PendingLrViewmodel>() {
    private val mAdapter by lazy { PendingLrAdapter() }
    override val inflate: InflateF<FragmentPendingLrBinding>
        get() = FragmentPendingLrBinding::inflate

    override fun initViewModel(): PendingLrViewmodel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPendingLr()
        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            setTitle("Pending LR")
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
        toolbar.setTitle("Pending LR")
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredList()
        }.launchIn(lifecycleScope)
        mAdapter.onItemClick = {
            PendingLrBottomSheetFragment.newInstance(it)
                .show(childFragmentManager, PendingLrBottomSheetFragment::class.simpleName)

        }
    }

}