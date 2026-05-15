package com.ssspvtltd.quick.ui.customerDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.databinding.FragmentCustomerListByCustomercodeBinding
import com.ssspvtltd.quick.ui.customerDetails.adapter.CustomerListAdapter
import com.ssspvtltd.quick.ui.customerDetails.adapter.CustomerListByCustomerCodeAdapter
import com.ssspvtltd.quick.ui.customerDetails.viewmodel.CustomerDetailsViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.observe
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
@AndroidEntryPoint
class CustomerListByCustomerCodeFragment
    : BaseFragment<FragmentCustomerListByCustomercodeBinding, CustomerDetailsViewModel>() {
    private val adapter by lazy { CustomerListByCustomerCodeAdapter() }
    override val inflate: InflateF<FragmentCustomerListByCustomercodeBinding>
        get() = FragmentCustomerListByCustomercodeBinding::inflate

    override fun initViewModel(): CustomerDetailsViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        registerObserver()      // ✅ here

        registerListener()
    }

    override fun onResume() {
        super.onResume()
    }


    private fun registerObserver() {

        viewModel.customerListbyCustomerCode.observe(viewLifecycleOwner) { list ->

            if (list.isNotEmpty()) {

                binding.recyclerView.visibility = View.VISIBLE
                binding.noDataText.visibility = View.GONE

                adapter.submitList(list)

            } else {

                binding.recyclerView.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE

                binding.noDataText.text = "No Data Found"
            }
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun registerListener() = with(binding) {


        false

        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        toolbar.setTitle("All Fair Customer Details")

        viewModel.observeSearch()

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {

                val searchText = binding.etSearch.text.toString().trim()

                if (searchText.length >= 2) {
                    viewModel.searchCustomer(searchText)
                }

                true
            } else {
                false
            }
        }
    }

}