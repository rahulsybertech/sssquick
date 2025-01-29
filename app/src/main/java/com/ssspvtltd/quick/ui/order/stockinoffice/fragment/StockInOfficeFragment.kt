package com.ssspvtltd.quick.ui.order.stockinoffice.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentStockInOfficeBinding
import com.ssspvtltd.quick.ui.order.stockinoffice.adapter.StockInOfficeAdapter
import com.ssspvtltd.quick.ui.order.stockinoffice.viewmodel.StockInOfficeViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StockInOfficeFragment : BaseFragment<FragmentStockInOfficeBinding, StockInOfficeViewModel>() {
    private val mAdapter by lazy { StockInOfficeAdapter() }
    override val inflate: InflateF<FragmentStockInOfficeBinding>
        get() = FragmentStockInOfficeBinding::inflate

    override fun initViewModel(): StockInOfficeViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStockInOffice()
        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            setTitle("Stock In Office")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
        initViews()
        registerListener()
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            mAdapter.submitList(viewModel.widgetList)
            println("IS_LIST_AVAILABLE ${viewModel.widgetList}, && $it")
        }

        viewModel.responseCodeOfSIO.observe(this) {
            if (it.equals("204")) {
                binding.noDataSio.text = viewModel.getMessage.value
                binding.noDataSio.visibility = View.VISIBLE
            }
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        toolbar.setTitle("Stock In Office")
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredList()
        }.launchIn(lifecycleScope)
        mAdapter.onItemClick = {
            StockInOfficeBottomSheetFragment.newInstance(it)
                .show(childFragmentManager, StockInOfficeBottomSheetFragment::class.simpleName)
        }
    }
}