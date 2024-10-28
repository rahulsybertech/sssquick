package com.ssspvtltd.quick_new.ui.checkincheckout.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.BaseFragment
import com.ssspvtltd.quick_new.base.InflateF
import com.ssspvtltd.quick_new.constants.ButtonType
import com.ssspvtltd.quick_new.constants.CheckInType
import com.ssspvtltd.quick_new.databinding.FragmentCheckInCheckOutBinding
import com.ssspvtltd.quick_new.ui.checkincheckout.adapter.CustomerAdapter
import com.ssspvtltd.quick_new.ui.checkincheckout.viewmodel.CheckInCheckOutViewModel
import com.ssspvtltd.quick_new.ui.main.MainActivity
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import com.ssspvtltd.quick_new.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CheckInCheckOutFragment :
    BaseFragment<FragmentCheckInCheckOutBinding, CheckInCheckOutViewModel>() {
    override val inflate: InflateF<FragmentCheckInCheckOutBinding> get() = FragmentCheckInCheckOutBinding::inflate
    override fun initViewModel(): CheckInCheckOutViewModel = getViewModel()
    private lateinit var mAdapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObserver()
        viewModel.getCustomer(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = CustomerAdapter()
        binding.recyclerView.layoutManager =
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
        binding.recyclerView.adapter = mAdapter
        // binding.recyclerView.addItemDecoration(ListSpacingItemDecoration(8.dp, 8.dp))
        registerListeners()
        binding.toolbar.setNavigationClickListener {
            if (requireActivity() is MainActivity) findNavController().navigateUp()
            else requireActivity().onBackPressed()
        }

//        binding.radioGroupId.setOnCheckedChangeListener((radioGroup, checkedId) -> {
//            if (checkedId == R.id.panNo){
//                pan_gst = "pan";
//                binding.gstAndPan.getText().clear();
//                int maxLength = 10;
//                binding.gstAndPan.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), new InputFilter.AllCaps()});
//                //   binding.gstAndPan.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
//            }else if (checkedId == R.id.gstNo){
//                pan_gst = "gst";
//                binding.gstAndPan.getText().clear();
//                int maxLength = 15;
//                binding.gstAndPan.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength), new InputFilter.AllCaps()});
//
//                //    binding.gstAndPan.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
//            }else {
//
//            }
//
//        });
//        binding.gstAndPan.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_CAP_WORDS | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//         binding.etRemark.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
    }

    private fun registerObserver() {
        viewModel.selectedTab.observe(this, ::handleTabSelection)
        viewModel.buttonType.observe(this, ::handleButtonVisibility)
        viewModel.isListAvailable.observe(this) {
            viewModel.updateCount()
            mAdapter.submitList(viewModel.widgetList)
            // binding.etRemark.text
        }
        viewModel.getRemark.observe(this) {
            if (it.equals("null", true))
                binding.etRemark.setText("")
            else binding.etRemark.setText(it.toString())
        }
        viewModel.counts.observe(this) {
            binding.orderInOfficeCount.text = it.first.toString()
            binding.orderInMarketCount.text = it.second.toString()
            binding.withCustomerCount.text = it.third.toString()
        }
        viewModel.ckeckoutSuccessData.observe(this) {
            binding.etSearch.text?.clear()
            viewModel.getCustomer(false)
            handleButtonVisibility(ButtonType.CHECK_IN)
            binding.update.isVisible = false
            //binding.etRemark.text.clear()
        }
        viewModel.addUpdateSuccessData.observe(this) {
            binding.etSearch.text?.clear()
            viewModel.getCustomer(false)
            binding.recyclerView.smoothScrollToPosition(0)
            handleButtonVisibility(ButtonType.CHECK_OUT)
        }
        viewModel.isUpdateButtonVisible.observe(this) {
            binding.update.isVisible = it
        }
        // viewModel.isListModified.observe(this) {
        //     binding.update.isVisible =
        //         it && viewModel.buttonType.value == ButtonType.CHECK_OUT && viewModel.selectedItem.isNotEmpty()
        // }
    }

    private fun navToHome() {
        if (requireActivity() is MainActivity) findNavController().navigateUp()
        else requireActivity().onBackPressed()
    }

    @OptIn(FlowPreview::class)
    private fun registerListeners() = with(binding) {
        orderInOffice.setOnClickListener { viewModel.setTabSelection(CheckInType.ORDER_IN_OFFICE) }
        orderInMarket.setOnClickListener {
            viewModel.setTabSelection(CheckInType.ORDER_IN_MARKET)
        }
        withCustomer.setOnClickListener { viewModel.setTabSelection(CheckInType.WITH_MARKETER) }
        other.setOnClickListener { viewModel.setTabSelection(CheckInType.OTHER) }
        checkIn.setOnClickListener { viewModel.addUpdateCustomer(::navToHome) }
        update.setOnClickListener { viewModel.addUpdateCustomer(::navToHome) }
        checkOut.setOnClickListener { viewModel.checkOutCustomer() }
        mAdapter.onItemClick = { viewModel.checkUncheckItem(it) }
        etRemark.doOnTextChanged { text, _, _, _ ->
            viewModel.remark = text?.toString()
             viewModel.validateUpdateButton()
            // if (viewModel.getRemark.value.equals(text.toString())){
             //     binding.update.isVisible = false
            // }else{
            //     binding.update.isVisible = true
            // }
        }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchData(it?.toString())
            viewModel.searchValue = binding.etSearch.text.toString()
        }.launchIn(lifecycleScope)


        etRemark.doAfterTextChanged {
            // binding.update.isVisible = true
            when {
                (it?.length ?: 0) >= 10 -> {
                    binding.error.text = "Max limit reached!"
                    binding.etRemark.setBackgroundResource(R.drawable.et_bg_red)
                }

                (it?.length ?: 0) < 10 -> {
                    binding.error.text = ""
                    binding.etRemark.setBackgroundResource(R.drawable.edit_bg)
                }
            }
        }

        swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.deep_orange_800))
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getCustomer(true)
            swipeRefreshLayout.isRefreshing = false

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleTabSelection(type: CheckInType) = with(binding) {
        when (type) {

            CheckInType.ORDER_IN_OFFICE -> {
                groupRemark.isVisible = false
                groupRecycler.isVisible = true
                swipeRefreshLayout.isVisible = true
                orderInOffice.setBackgroundResource(R.color.white)
                orderInMarket.setBackgroundResource(R.color.grey_200)
                withCustomer.setBackgroundResource(R.color.grey_200)
                other.setBackgroundResource(R.color.grey_200)
            }

            CheckInType.ORDER_IN_MARKET -> {
                groupRemark.isVisible = false
                groupRecycler.isVisible = true
                swipeRefreshLayout.isVisible = true
                orderInMarket.setBackgroundResource(R.color.white)
                orderInOffice.setBackgroundResource(R.color.grey_200)
                withCustomer.setBackgroundResource(R.color.grey_200)
                other.setBackgroundResource(R.color.grey_200)
            }

            CheckInType.WITH_MARKETER -> {
                groupRemark.isVisible = false
                groupRecycler.isVisible = true
                swipeRefreshLayout.isVisible = true
                orderInMarket.setBackgroundResource(R.color.grey_200)
                orderInOffice.setBackgroundResource(R.color.grey_200)
                withCustomer.setBackgroundResource(R.color.white)
                other.setBackgroundResource(R.color.grey_200)
            }

            CheckInType.OTHER -> {
                groupRemark.isVisible = true
                groupRecycler.isVisible = false
                swipeRefreshLayout.isVisible = false
                orderInMarket.setBackgroundResource(R.color.grey_200)
                orderInOffice.setBackgroundResource(R.color.grey_200)
                withCustomer.setBackgroundResource(R.color.grey_200)
                other.setBackgroundResource(R.color.white)
            }
        }

        mAdapter.checkInType = type
        mAdapter.notifyDataSetChanged()

    }

    private fun handleButtonVisibility(buttonType: ButtonType) = with(binding) {
        when (buttonType) {
            ButtonType.CHECK_OUT -> {
                checkOut.isVisible = true
                // update.isVisible = true
                checkIn.isVisible = false
            }

            else -> {
                checkOut.isVisible = false
                // update.isVisible = false
                checkIn.isVisible = true
            }
        }
    }
}