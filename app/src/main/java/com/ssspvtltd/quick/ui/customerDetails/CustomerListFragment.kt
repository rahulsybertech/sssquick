package com.ssspvtltd.quick.ui.customerDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentAllAccountListBinding
import com.ssspvtltd.quick.ui.customerDetails.adapter.CustomerListAdapter
import com.ssspvtltd.quick.ui.customerDetails.viewmodel.CustomerDetailsViewModel
import com.ssspvtltd.quick.ui.order.goodsreturn.activity.GoodsReturnActivity

import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
@AndroidEntryPoint
class CustomerListFragment
    : BaseFragment<FragmentAllAccountListBinding, CustomerDetailsViewModel>() {
    private val adapter by lazy { CustomerListAdapter() }
    override val inflate: InflateF<FragmentAllAccountListBinding>
        get() = FragmentAllAccountListBinding::inflate

    override fun initViewModel(): CustomerDetailsViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        registerObserver()      // ✅ here
        callPendingOrderApi()   // ✅ here
        registerListener()
    }
    private fun callPendingOrderApi() {
        lifecycleScope.launch {
            val customerJob = launch {
                viewModel.fatchAllAccountList()
            }
            customerJob.join()
        }
    }

    private fun registerObserver() {
        viewModel.allAccountList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(list)
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        }

        viewModel.deleteAccountForID.observe(viewLifecycleOwner) { isSuccess ->

            if (isSuccess) {
                showToast("Delete Account Successfully")
                viewModel.fatchAllAccountList()
            } else {

                showToast("Customer Add Failed")

            }
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun registerListener() = with(binding) {


        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        toolbar.setTitle("Garment Fair Customer Details Reports")
        adapter.onItemEditClick = {
            val intent = Intent(requireActivity(), CustomerDetailsActivity::class.java)
            intent.putExtra("id",it.id)
            startActivity(intent)

        }
        adapter.onItemDeleteClick = {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteAccountForIDParam(it.id)
                }
                .setNegativeButton("No", null)
                .show()
        }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.filterList( viewModel.searchValue )
        }.launchIn(lifecycleScope)
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}