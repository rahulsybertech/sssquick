package com.ssspvtltd.quick.ui.order.pending.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentPendingOrderBinding
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.pending.FilterRequest
import com.ssspvtltd.quick.ui.order.add.adapter.SalePartyAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderAdapter
import com.ssspvtltd.quick.ui.order.pending.adapter.PendingOrderByCustomerAdapter
import com.ssspvtltd.quick.ui.order.pending.viewmodel.PendingOrderViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import com.ssspvtltd.quick.utils.extension.showKeyboard
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PendingOrderByCustomer
    : BaseFragment<FragmentPendingOrderBinding, PendingOrderViewModel>() {
    private lateinit var salePartyAdapter: SalePartyAdapter
    var selectedCustomer="Marketer Wise"
    var isFliter=false
    private val filterOptions = listOf("Marketer Wise","Customer Wise")
    private lateinit var customerAdapter: ArrayAdapter<String>
    private lateinit var statusDropDownAdapter: ArrayAdapter<String>
    private val mAdapter by lazy { PendingOrderByCustomerAdapter() }
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
        lifecycleScope.launch {
            // Launch both API calls in parallel

          /*  val pendingOrderJob = launch {
                val filterRequest = FilterRequest(null, null, null, null, true, "1", "100")
                viewModel.fetchOrdersPage(1, mutableListOf())

            }*/

            val customerJob = launch {
                viewModel.getCustomer(true)
            }
            //   launch {     viewModel.getCount() }

        //    viewModel.fetchCountInBackground()
            // Optional: wait for both
         //   pendingOrderJob.join()
            customerJob.join()
        }
    }

    fun CustomerData.toSalepartyData(): SalepartyData {
        return SalepartyData(
            accountID = this.id,
            accountName = this.accountName,
            rno = null // or assign any field if needed
        )
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            mAdapter.submitList(viewModel.widgetList)
            if(viewModel.widgetList.isNotEmpty()){
                binding.recyclerView.visibility=View.VISIBLE
                binding.noDataText.visibility = View.GONE
            }else{
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerView.visibility=View.GONE
            }
        }

      /*  viewModel.responseCodeOfPendingList.observe(this) {
            println("GETTING_MESSAGE_RESPONSE $it, && ${viewModel.responseMessageOfPendingList}")
            //  binding.etCustomerCode.hideKeyBoard()
         *//*   if (it == "204") {
                binding.recyclerView.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE
                binding.noDataText.text = viewModel.responseMessageOfPendingList.value ?: "No Data Found"
            } else {
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }*//*
        }*/

        viewModel.customerDataResp.observe(this) { customerList ->

            val salePartyList = customerList.map { it.toSalepartyData() }
            salePartyAdapter = SalePartyAdapter(requireContext(), R.layout.item_saleparty, salePartyList)

            binding.etCustomerCode.threshold = 1
            binding.etCustomerCode.setAdapter(salePartyAdapter)

            binding.etCustomerCode.setOnItemClickListener { parent, _, position, _ ->
                val salePartyItem = salePartyAdapter.getItem(position)
                hideKeyboard()
                // Call only first page, rest will load in background
                salePartyItem?.accountID?.let { customerId ->
                    viewModel.fetchOrdersPageByCustomerFirstPage(customerId)
                }

                binding.tilSaletParty.isErrorEnabled =
                    !(salePartyItem.accountName.isNotNullOrBlank() || binding.tilSaletParty.isErrorEnabled)
            }
        }

    }

    private fun initViews() = with(binding) {
        binding.tilSaletParty.visibility=View.VISIBLE
        binding.etCustomerCode.requestFocus()
        binding.etCustomerCode.showKeyboard()
        recyclerView.adapter = mAdapter
      //  recyclerViewCustomerby.adapter = mAdapter
        toolbar.setTitle("Pending Order")
        viewModel.countText.observe(viewLifecycleOwner) { countString ->
            binding.tvCount.text = countString
        }

        binding.etCustomerCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val original = s.toString()
                val filtered = original.replace(Regex("\\s{2,}"), " ") // replaces 2+ spaces with 1

                if (original != filtered) {
                    binding.etCustomerCode.setText(filtered)
                    binding.etCustomerCode.setSelection(filtered.length) // move cursor to end
                }
            }
        })
        /* binding.etCustomerCode.setOnFocusChangeListener { v, hasFocus ->
             if (!hasFocus) {
                 if (salePartyData?.any { it.accountName == binding.etCustomerCode.text.toString() } == false) {
                     binding.etCustomerCode.text.clear()
                 }
             }
             if (!binding.etCustomerCode.isPopupShowing) {
                 binding.etCustomerCode.showDropDown()
             }
         }*/
        binding.etCustomerCode.setOnClickListener {
            if (!binding.etCustomerCode.isPopupShowing) {
                binding.etCustomerCode.showDropDown()
            }
        }

        binding.tilSaletParty.setEndIconOnClickListener {
            binding.etCustomerCode.text.clear()
            binding.recyclerView.visibility = View.GONE
            viewModel.clearPendingListData()
        }

        /* binding.etSearch.imeOptions = EditorInfo.IME_ACTION_SEARCH
         binding.etSearch.setRawInputType(InputType.TYPE_CLASS_TEXT)*/

        setupCustomerDropdown()
    }
    private fun setupCustomerDropdown() {
        customerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterBy.adapter = customerAdapter

        var isSpinnerInitialized = false
        var lastSelectedPosition = -1

        binding.spinnerFilterBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true
                    lastSelectedPosition = position
                    return
                }

                if (lastSelectedPosition == position) return // prevent repeated API call
                lastSelectedPosition = position

                selectedCustomer = filterOptions[position]

                lifecycleScope.launch {
                    delay(0L)

                    if (selectedCustomer == "Customer Wise") {
                        isFliter=true
                        binding.tilSaletParty.visibility = View.VISIBLE

                        // Ensure view is laid out before requesting focus
                        binding.etCustomerCode.post {
                            binding.etCustomerCode.requestFocus()
                            binding.etCustomerCode.showKeyboard() // Optional: show keyboard
                        }
                    } else {
                        binding.tilSaletParty.visibility = View.GONE
                    }

                    if (selectedCustomer == "Marketer Wise") {
                        isFliter=true
                        binding.etCustomerCode.text.clear()
                        viewModel.fetchOrdersPage(1, mutableListOf())
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
            PendingOrderCustomerDetailsBottomSheetFragment.newInstance(it).show(
                childFragmentManager, PendingOrderCustomerDetailsBottomSheetFragment::class.simpleName
            )
        }
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }
}