package com.ssspvtltd.quick.ui.followupcall
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentFollowupcallBinding
import com.ssspvtltd.quick.di.PrefHelperEntryPoint.Companion.prefHelper
import com.ssspvtltd.quick.ui.followupcall.adapter.FollowUpCallsAdapter
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
@AndroidEntryPoint
class FollowUpCallsFragment
    : BaseFragment<FragmentFollowupcallBinding, FollowUpCallsViewModel>() {
    private val adapter by lazy { FollowUpCallsAdapter() }
    private var selectedType = "1" // Existing Customer
    override val inflate: InflateF<FragmentFollowupcallBinding>
        get() = FragmentFollowupcallBinding::inflate

    override fun initViewModel(): FollowUpCallsViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        registerObserver()      // ✅ here
        setupTabs()
        registerListener()
    }
    private fun setupTabs() = with(binding) {

        cardExisting.setOnClickListener {
            if (selectedType != "1") {
                etSearch.setText("")
                etSearch.clearFocus()
                binding.root.requestFocus()
                selectedType = "1"
                selectTab(0)
                callPendingOrderApi(selectedType)
            }
        }

        cardOther.setOnClickListener {
            if (selectedType != "2") {
                selectedType = "2"
                etSearch.setText("")
                etSearch.clearFocus()

                binding.root.requestFocus()
                selectTab(1)
                callPendingOrderApi(selectedType)
            }
        }

        cardLeads.setOnClickListener {
            if (selectedType != "3") {
                selectedType = "3"
                etSearch.setText("")
                etSearch.clearFocus()

                binding.root.requestFocus()
                selectTab(2)
                callPendingOrderApi(selectedType)
            }
        }
    }

    private fun selectTab(position: Int) = with(binding) {

        // Reset all tabs
        cardExisting.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
        cardOther.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
        cardLeads.setCardBackgroundColor(Color.parseColor("#F5F5F5"))

        txtExisting.setTextColor(Color.parseColor("#333333"))
        txtOther.setTextColor(Color.parseColor("#333333"))
        txtLeads.setTextColor(Color.parseColor("#333333"))

        // Selected tab
        when (position) {
            0 -> {
                cardExisting.setCardBackgroundColor(Color.parseColor("#E53935"))
                txtExisting.setTextColor(Color.WHITE)
                adapter.selectedType="1"
            }

            1 -> {
                cardOther.setCardBackgroundColor(Color.parseColor("#E53935"))
                txtOther.setTextColor(Color.WHITE)
                adapter.selectedType="2"
            }

            2 -> {
                cardLeads.setCardBackgroundColor(Color.parseColor("#E53935"))
                txtLeads.setTextColor(Color.WHITE)
                adapter.selectedType="3"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        callPendingOrderApi(selectedType)
        binding.etSearch.clearFocus()
        binding.recyclerView.requestFocus()
    }
    private fun callPendingOrderApi(selectedType: String) {
        lifecycleScope.launch {
            val customerJob = launch {
                viewModel.getfollowpCallList(prefHelper.getAccountId(),selectedType)
            }
            customerJob.join()
        }
    }
    private fun callUpdateLeadApi(data: LeadData,remark:String) {
        lifecycleScope.launch {
            val customerJob = launch {
                viewModel.updateLead(data.id,remark)
            }
            customerJob.join()
        }


        viewModel.updateLeadResponse.observe(viewLifecycleOwner) { list ->
            showToast(list)
            // callPendingOrderApi(selectedType)
        }
    }


    private fun registerObserver() {
        viewModel.getAllLeadByUserIDtList.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {

                binding.recyclerView.visibility = View.VISIBLE

                adapter.submitList(
                    list.sortedByDescending { it.leadNo }
                )

                binding.recyclerView.post {
                    val layoutManager =
                        binding.recyclerView.layoutManager as LinearLayoutManager

                    val lastVisible = layoutManager.findLastVisibleItemPosition() + 1

                    binding.tvPositionCount.text = "$lastVisible/${list.size}"
                }

            } else {
                binding.recyclerView.visibility = View.GONE
                binding.tvPositionCount.text = "0/0"
            }
        }

        viewModel.deleteAccountForID.observe(viewLifecycleOwner) { isSuccess ->

            if (isSuccess) {
                showToast("Delete Account Successfully")
                // viewModel.getAllLeadtList()
            } else {

                showToast("Customer Add Failed")

            }
        }
    }

    private fun initViews() = with(binding) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        //  recyclerView.adapter = adapter
        binding.etSearch.apply {
            isFocusableInTouchMode = true
            isFocusable = true
        }
        /*     binding.recyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                 if (oldBottom > bottom) {
                     binding.recyclerView.post {
                         binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                         *//*          binding.etSearch.isFocusable = false
                              binding.etSearch.isFocusableInTouchMode = false*//*
                }
            }
        }*/
        adapter.selectedType="1"
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val currentPosition = layoutManager.findFirstVisibleItemPosition() + 1
                val totalCount = adapter.itemCount

                binding.tvPositionCount.text = "$currentPosition/$totalCount"
            }
        })
    }

    private fun registerListener() = with(binding) {

/*        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        false*/

        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        toolbar.setTitle("Follow Up Calls")
        adapter.onItemEditClick = { leadData, remark,position ->
            leadData.isFollowupDone = true
            adapter.notifyItemChanged(position)
            callUpdateLeadApi(leadData, remark)
        }
        adapter.onRemarkFocus = {  remark ->
            /*    recyclerView.post {
                    val lm = recyclerView.layoutManager as LinearLayoutManager
                    val view = lm.findViewByPosition(remark)

                    if (view != null) {
                        recyclerView.smoothScrollBy(0, 150)
                    }
                }*/
        }
        adapter.onItemDeleteClick = {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${it.mobileNo}")
            startActivity(intent)
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