package com.ssspvtltd.quick.ui.create_gr

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentCreateGrBinding
import com.ssspvtltd.quick.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick.model.ARG_ADD_ITEM_LIST
import com.ssspvtltd.quick.model.ARG_PENDING_ORDER_ID
import com.ssspvtltd.quick.model.SubPartyGRData
import com.ssspvtltd.quick.model.TransportMasterData
import com.ssspvtltd.quick.model.alert.AlertMsg
import com.ssspvtltd.quick.model.checkincheckout.CustomerData
import com.ssspvtltd.quick.model.gr.GoodsReturnDataGr
import com.ssspvtltd.quick.model.order.add.DispatchTypeList
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartyNewList.AllStation
import com.ssspvtltd.quick.model.order.add.salepartyNewList.DefTransport
import com.ssspvtltd.quick.model.order.add.salepartyNewList.SubParty
import com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnData
import com.ssspvtltd.quick.ui.order.add.activity.AddImageActivity
import com.ssspvtltd.quick.ui.order.add.adapter.DefaultTransportAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.DispatchAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.PackDataAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.PurchasePartyAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SalePartyAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SchemeAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.StationAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SubPartyAdapter
import com.ssspvtltd.quick.ui.order.add.fragment.AddItemBottomSheetFragment
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddOrderViewModel
import com.ssspvtltd.quick.utils.SharedEditImageUriList
import com.ssspvtltd.quick.utils.extension.getParcelableArrayListExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
import com.ssspvtltd.quick.utils.extension.observe
import com.ssspvtltd.quick.utils.hideKeyBoard
import com.ssspvtltd.quick.utils.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CreateGRFragment : BaseFragment<FragmentCreateGrBinding, AddOrderViewModel>() {
    // private val addItemViewModel by activityViewModels<AddItemViewModel>()
    override val inflate: InflateF<FragmentCreateGrBinding> get() = FragmentCreateGrBinding::inflate
    override fun initViewModel(): AddOrderViewModel = getViewModel()

    private val addItemViewModel by viewModels<AddItemViewModel>()

    private var salePartyId: String = ""

    // changes
    private var subPartyId: String = ""
    private var subPartyIdMainBranch: String = ""
    private var transportIDdMainBranch: String = ""
    private var stationIDdMainBranch: String = ""
    private var purchasePartyId: String = ""
    private var pvtMarka: String = "*"
    private var isPvtMarka: Boolean = false
    private var isTransPort: Boolean = true
    private var bookingStationId: String = ""
    private var transportId: String = ""
    private var courierId: String = ""
    private var dispatchId: String = ""
    private var schemeId: String = ""
    private var selectedStatus: String = "PENDING"

    private var purchasePartyData: List<PurchasePartyData>? = emptyList()
    private var nicNaneData: List<PurchasePartyData>? = emptyList()
    private var purchasePartyDataWithNicName: List<PurchasePartyData>? = emptyList()
    private var salePartyData: List<SalepartyData>? = emptyList()
    private var subPartyData: List<SubParty>? = emptyList()
    private var dispatchTypeData: List<DispatchTypeList>? = emptyList()
    private var stationData: List<AllStation>? = emptyList()
    private var stationDataWithRemark: ArrayList<AllStation>? = arrayListOf()
    private var stationDataWithSubparty: ArrayList<AllStation>? = arrayListOf()
    private var schemeData: List<SchemeData>? = emptyList()
    private var transportData: MutableList<DefTransport> = mutableListOf()

    // private var transportData: List<DefTransport>? = emptyList()
    private var transportDataWithRemark: ArrayList<DefTransport>? = arrayListOf()
    private var transportDataWithSubparty: ArrayList<DefTransport>? = arrayListOf()


    private lateinit var subPartyAdapter: SubPartyAdapter
    private lateinit var defaultTransportAdapter: DefaultTransportAdapter
    private lateinit var stationAdapter: StationAdapter
    private lateinit var purchasePartyAdapter: PurchasePartyAdapter
    private lateinit var purchasePartyAdapterWithNickName: PurchasePartyAdapter
    private lateinit var nickNameAdapter: PurchasePartyAdapter
    private lateinit var schemeAdapter: SchemeAdapter
    private lateinit var dispatchAdapter: DispatchAdapter
    private var isNickNameSelected = true
    private var isSchemeHasData = false
    private lateinit var salePartyAdapter: SalePartyAdapter
    private var editData: EditOrderDataNew? = null
    private var editDataGr: GoodsReturnDataGr? = null
    private val addItemAdapter by lazy { PackDataAdapter() }
    private var statusOptions = listOf("PENDING", "HOLD")
    private var statusOptionsOnEdit = listOf("PENDING", "HOLD", "CANCEL")
    private var isSubPartyRadioSelect = true
    private lateinit var statusDropDownAdapter: ArrayAdapter<String>
    private var traceIdentifier: String? = "00000000-0000-0000-0000-000000000000"
    private var IS_EDITABLE: Boolean? = false
    private var isNichNameSelect: Boolean? = false
    private var isApiCalled = false // Flag to track API call
    private var isSubPartyRemark = false



    companion object {
        const val TAG = "TaG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            delay(1000)
            isNickNameSelected=false
            val pendingOrderId = activity?.intent?.getStringExtra(ARG_PENDING_ORDER_ID)
            if (pendingOrderId.isNullOrEmpty()) {
                viewModel.initAllDataGr("")
                addItemViewModel.getPackDataList()
                viewModel.getCustomer(true)
                addItemViewModel.getPackType()
                addItemViewModel.getItemsListGr("")


            } else {
                viewModel.initAllDataGr(pendingOrderId)
            }

        }
        /*    lifecycleScope.launch {
                delay(1000)
                isNickNameSelected=false
                viewModel.initializeCreateGR()
                addItemViewModel.getPackDataList()
                viewModel.getCustomer(true)
                addItemViewModel.getPackType()
            }*/

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

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

        binding.etSubParty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val original = s.toString()
                val filtered = original.replace(Regex("\\s{2,}"), " ") // replaces 2+ spaces with 1

                if (original != filtered) {
                    binding.etSubParty.setText(filtered)
                    binding.etSubParty.setSelection(filtered.length) // move cursor to end
                }
            }
        })
        binding.etTransport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val original = s.toString()
                val filtered = original.replace(Regex("\\s{2,}"), " ") // replaces 2+ spaces with 1

                if (original != filtered) {
                    binding.etTransport.setText(filtered)
                    binding.etTransport.setSelection(filtered.length) // move cursor to end
                }
            }
        })
        binding.etNicName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val original = s.toString()
                val filtered = original.replace(Regex("\\s{2,}"), " ") // replaces 2+ spaces with 1

                if (original != filtered) {
                    binding.etNicName.setText(filtered)
                    binding.etNicName.setSelection(filtered.length) // move cursor to end
                }
            }
        })
        binding.etPurchaseParty.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val original = s.toString()
                val filtered = original.replace(Regex("\\s{2,}"), " ") // replaces 2+ spaces with 1

                if (original != filtered) {
                    binding.etPurchaseParty.setText(filtered)
                    binding.etPurchaseParty.setSelection(filtered.length) // move cursor to end
                }
            }
        })


        if (viewModel.pendingOrderID.isNotNullOrBlank()) {
            binding.star1.isEnabled = false
            binding.star3.isEnabled = false
        }
        val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        binding.etMarketerCode.setText(currentDate)
        statusDropDownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusOptions
        )

        binding.autoCompleteStatus.setAdapter(statusDropDownAdapter)

        binding.autoCompleteStatus.setOnItemClickListener { parent, _, position, id ->
            selectedStatus = parent.getItemAtPosition(position).toString()
        }
        binding.autoCompleteStatus.setText("PENDING", false)

        binding.etParcelQty.inputType = InputType.TYPE_CLASS_NUMBER
        binding.etParcelQty.filters = arrayOf(InputFilter.LengthFilter(3))


        binding.etParcelQty.addTextChangedListener(object : TextWatcher {
            private var previousText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s?.toString() ?: ""
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    binding.tilParcelQty.isErrorEnabled = false
                    binding.tilParcelQty.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val input = it.toString()

                    if (input.isEmpty()) return

                    // Remove leading zeros
                    val cleanInput = input.trimStart('0').ifEmpty { "0" }

                    // Convert to integer
                    val qty = cleanInput.toIntOrNull() ?: return

                    if (qty > 1000) {
                        // Exceeded max limit
                        binding.tilParcelQty.isErrorEnabled = true
                        binding.tilParcelQty.error = "Maximum allowed is 999"
                        binding.etParcelQty.setText(previousText)
                        binding.etParcelQty.setSelection(previousText.length)
                    } else {
                        if (input != cleanInput) {
                            binding.etParcelQty.setText(cleanInput)
                            binding.etParcelQty.setSelection(cleanInput.length)
                        }
                    }
                }
            }
        })

        binding.toolbar.apply {
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
            if (viewModel.pendingOrderID.isNotNullOrBlank()) {
                setTitle("Edit Order")
                binding.etCustomerCode.isEnabled = false
                binding.etSubPartyRemark.isEnabled = false
                binding.etPurchasePartyNew.isEnabled = false
                binding.etNicName.isEnabled = false
                binding.radioSubparty.isEnabled = false
                binding.radioSubpartyRemark.isEnabled = false
                binding.etSubParty.isEnabled = false
                binding.etDispatchType.isEnabled = false
                binding.etTransport.isEnabled = false
                binding.etStation.isEnabled = false
                binding.etScheme.isEnabled = false
                binding.radioByNickName.isEnabled = false
                binding.radioBySupplierName.isEnabled = false
                binding.tilPurchaseParty.isEnabled = false
                binding.etDiscription.isEnabled = false
                binding.etpvtMarka.isEnabled = false
                binding.tvDispatchFromDate.isEnabled = false
                binding.tvDispatchToDate.isEnabled = false
                binding.tvItemImage.isEnabled = false

            } else {
                nickNameList()
                setTitle("Create GR")
            }

        }

        binding.etCustomerCode.doAfterTextChanged {
            salePartyId = ""
        }

        binding.tilSaletParty.setEndIconOnClickListener {
            //Abhinav
            binding.etCustomerCode.text.clear()
            binding.etAvailableLimit.text?.clear()
            binding.etAverageDays.text?.clear()
            binding.etSubParty.text?.clear()
          //  binding.etNicName.text?.clear()
        //    binding.etTransport.text?.clear()
         //   binding.etStation.text?.clear()
          //  binding.etScheme.text?.clear()
          //  binding.etPurchaseParty.text?.clear()
            binding.etDiscription.text?.clear()
            binding.etpvtMarka.text?.clear()
            binding.tvDispatchFromDate.text = ""
            binding.tvDispatchToDate.text = ""
        }
        binding.tilSubParty.setEndIconOnClickListener {
            //Abhinav

         //   transportData.clear()
            stationData= emptyList()
          //  binding.etTransport.text?.clear()
            binding.etSubParty.text?.clear()
            binding.etStation.text?.clear()
        }

        binding.tilTransport.setEndIconOnClickListener {
            //Abhinav
        //    stationData= emptyList()
            viewModel.onSearchQueryChanged("")
            binding.etTransport.text?.clear()
            binding.etStation.text?.clear()
        }

        binding.tilNickNameList.setEndIconOnClickListener {
            //Abhinav

            isNickNameSelected==false
            binding.tilPurchasePartyNew.visibility=View.GONE
            binding.tilPurchaseParty.visibility=View.VISIBLE
            binding.etNicName.text?.clear()
            binding.etPurchasePartyNew.text?.clear()
            binding.etPurchaseParty.text?.clear()
           /* addItemViewModel.clearWidgetList()
            addItemViewModel.listDataChanged()
            viewModel.addImageDataList.clear()
            addItemViewModel.packTypeDataList= emptyList()*/
        }

        binding.tilPurchaseParty.setEndIconOnClickListener {
            //Abhinav

            binding.etNicName.text?.clear()
            binding.etPurchaseParty.text?.clear()

           /* addItemViewModel.clearWidgetList()
            addItemViewModel.listDataChanged()
            viewModel.addImageDataList.clear()
            addItemViewModel.packTypeDataList= emptyList()
            if (viewModel.addImageDataList.size > 0) {
                binding.tvAddItem.isEnabled = false
                binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
            } else {
                binding.tvAddItem.isEnabled = true
                binding.tvAddItem.alpha = 1.0f
            }*/
        }


        binding.etSubParty.doAfterTextChanged {
            subPartyId = ""
        }

        binding.etTransport.doAfterTextChanged {
            transportId = ""
        }

        binding.etPurchasePartyNew.doAfterTextChanged {
            purchasePartyId = ""
        }

        binding.etPurchaseParty.doAfterTextChanged {
            purchasePartyId = ""

        }

        binding.placeOrder.setOnClickListener {
            if (validate())
                binding.apply {
                    var totalQty = 0
                    var totalAmount = 0.0

                    addItemViewModel.packTypeDataList.forEach {
                        if (it.amount.isNotNullOrBlank() && it.qty.isNotNullOrBlank()) {
                            totalAmount += (it.amount ?: "0.0").toDouble()
                            totalQty += (it.qty ?: "0.0").toInt()
                        }
                    }
                    val hashMap: HashMap<String, RequestBody?> = HashMap()
                    println("PLACING_ORDER 3 ${Gson().toJson(hashMap)}")
                    hashMap["CustomerId"] = salePartyId.toRequestBody()
                    println("PLACING_ORDER 2 ${Gson().toJson(salePartyId.toRequestBody())}")
                    if (subPartyId == "00000000-0000-0000-0000-000000000000") {
                        hashMap["SubPartyId"] = "".toRequestBody()
                    } else {

                        if(subPartyId.equals("null")){
                            hashMap["SubPartyId"] = "".toRequestBody()
                        }
                        else{
                            hashMap["SubPartyId"] = subPartyId.toRequestBody()
                        }
                    }
                    if(isSubPartyRemark){
                        hashMap["SubPartyId"] = "".toRequestBody()
                        if(isTransPort){
                            hashMap["TransportId"] = transportId.toRequestBody()
                        }else{
                            hashMap["CourierId"] = courierId.toRequestBody()
                        }

                    }else{
                        if(isTransPort){
                            hashMap["TransportId"] = transportId.toRequestBody()
                        }else{
                            hashMap["CourierId"] = courierId.toRequestBody()
                        }
                    }
                    hashMap["SupplierId"] = purchasePartyId.toRequestBody()
                    hashMap["CourierNo"] = binding.etBiltyCourierNo.text!!.trim().toString().toRequestBody()
                    hashMap["ParcelQty"] = etParcelQty.text.toString().trim().toRequestBody()
                    hashMap["TraceIdentifier"] = traceIdentifier?.toRequestBody()
                    val currentDate: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    hashMap["GrInDate"] = currentDate.toRequestBody()
                    println("PLACING_ORDER 0 $selectedStatus)}")
                    if (viewModel.pendingOrderID.isNotNullOrBlank()) hashMap["id"] =
                        (editData?.id ?: "").toRequestBody()
                    Log.e("hashmap",hashMap.toString())
                    viewModel.placeOrderGr(hashMap)

                    binding.placeOrder.isEnabled=false
                }
        }
        binding.tvAddItem.setOnClickListener {

            openAddBottomSheet()


        }
        binding.tvItemImage.setOnClickListener {
            addImageResultLauncher.launch(
                AddImageActivity.newStartIntent(requireContext(), viewModel.addImageDataList)
            )
        }

        binding.recyclerViewAddItem.adapter = addItemAdapter
        addItemAdapter.onEditClick =
            { openAddBottomSheet(addItemViewModel.packTypeDataList.indexOf(it)) }
        addItemAdapter.onDeleteClick = { addItemViewModel.deletePackTypeData(it) }

        // Customer selection
        binding.etCustomerCode.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (salePartyData?.any { it.accountName == binding.etCustomerCode.text.toString() } == false) {
                    binding.etCustomerCode.text.clear()
                }
            }
            if (!binding.etCustomerCode.isPopupShowing) {
                binding.etCustomerCode.showDropDown()
            }
        }
        binding.etCustomerCode.setOnClickListener {
            if (!binding.etCustomerCode.isPopupShowing) {
                binding.etCustomerCode.showDropDown()
            }
        }

// Sub-party selection with customer check
        binding.etSubParty.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (subPartyData?.any { it.subPartyName == binding.etSubParty.text.toString() } == false) {
                    binding.etSubParty.text.clear()
                }
            } else {
                // When gaining focus, check if customer is selected
                val customerSelected = salePartyData?.any { it.accountName == binding.etCustomerCode.text.toString() } == true
                if (!customerSelected) {
                    binding.etSubParty.clearFocus()
                    binding.etCustomerCode.requestFocus()
                    binding.tilSaletParty.isErrorEnabled = true
                    binding.tilSaletParty.setError("You need to select a valid customer first")
                    //   Toast.makeText(context, "Please select a valid customer first", Toast.LENGTH_SHORT).show()
                } else if (!binding.etSubParty.isPopupShowing) {
                    binding.etSubParty.showDropDown()
                }
            }
        }
        binding.etSubParty.setOnClickListener {
            val customerSelected = salePartyData?.any { it.accountName == binding.etCustomerCode.text.toString() } == true
            if (!customerSelected) {
                binding.etCustomerCode.requestFocus()
                binding.tilSaletParty.isErrorEnabled = true
                binding.tilSaletParty.setError("You need to select a valid customer first")
                //   Toast.makeText(context, "Please select a valid customer first", Toast.LENGTH_SHORT).show()
            } else if (!binding.etSubParty.isPopupShowing) {
                binding.etSubParty.showDropDown()
            }
        }





        binding.etTransport.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (transportDataWithRemark?.any { it.transportName == binding.etTransport.text.toString() } == false) {
                    binding.etCustomerCode.text.clear()
                }
            }
            if (!binding.etTransport.isPopupShowing) {
                binding.etTransport.showDropDown()
            }
        }
        binding.etTransport.setOnClickListener {
            if (!binding.etTransport.isPopupShowing) {
                binding.etTransport.showDropDown()
            }
        }
        binding.etTransport.addTextChangedListener {
            val input = it?.toString()?.trim().orEmpty()
            viewModel.onSearchQueryChanged(input)
        }


        binding.etPurchaseParty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // Call API only if it hasn't been called before
                if (!isApiCalled) {
                 //   viewModel.getPurchaseParty(null,null, false)
                    viewModel.getPurchasePartyInGr(null,null, false)
                    if (::purchasePartyAdapter.isInitialized) purchasePartyAdapter.updateType(false)
                    isNickNameSelected = false
                    //   callPurchasePartyApi()
                    isApiCalled = true // Mark API as called
                }
            } else {
                isNickNameSelected = false
                if (isNickNameSelected) {
                    if (purchasePartyData?.any { it.nickName.equals(binding.etPurchaseParty.text.trim().toString()) } == false) {
                        binding.etPurchaseParty.text.clear()
                    }
                } else {
                    if (purchasePartyData?.any { it.accountName.equals(binding.etPurchaseParty.text.trim().toString()) } == false) {
                        binding.etPurchaseParty.text.clear()
                    }
                }
            }

            // Show dropdown when the field is clicked
            if (!binding.etPurchaseParty.isPopupShowing) {
                binding.etPurchaseParty.showDropDown()
            }
        }

        binding.etPurchaseParty.setOnClickListener {
            if (!binding.etPurchaseParty.isPopupShowing) {
                binding.etPurchaseParty.showDropDown()
            }
        }



        binding.etPurchasePartyNew.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                /*    isNickNameSelected=false
                    if (isNickNameSelected) {
                        if (purchasePartyDataWithNicName?.any { it ->
                                it.nickName.equals(
                                    binding.etPurchasePartyNew.text.trim().toString()
                                )
                            } == false) {
                            binding.etPurchasePartyNew.text.clear()
                        }
                    }*/
                if (purchasePartyDataWithNicName?.any { it ->
                        it.accountName.equals(
                            binding.etPurchasePartyNew.text.trim().toString()
                        )
                    } == false) {
                    binding.etPurchasePartyNew.text.clear()
                }


            }

            if (!binding.etPurchasePartyNew.isPopupShowing) {
                binding.etPurchasePartyNew.showDropDown()
            }
        }


        binding.etPurchasePartyNew.setOnClickListener {
            if (!binding.etPurchasePartyNew.isPopupShowing) {
                binding.etPurchasePartyNew.showDropDown()
            }
        }

        binding.etNicName.setOnFocusChangeListener { v, hasFocus ->


            /* if (purchasePartyData!!.size>1){

             }*/
            if (!hasFocus) {
                isNickNameSelected=true
                if (isNickNameSelected) {
                    var isMatchFound = false
                    nicNaneData?.forEach { it ->
                        if (it.nickName.equals(binding.etNicName.text.trim().toString(), ignoreCase = true)) {

                            isMatchFound = true
                        }
                    }

                    if (!isMatchFound) {
                        binding.etNicName.text.clear()
                    }
                } else {
                    if (nicNaneData?.any { it ->
                            it.accountName.equals(
                                binding.etNicName.text.trim().toString()
                            )
                        } == false) {
                        binding.etNicName.text.clear()
                    }
                }

            }

            if (!binding.etNicName.isPopupShowing) {
                binding.etNicName.showDropDown()
            }
        }
        binding.etNicName.setOnClickListener {

            if (!binding.etNicName.isPopupShowing) {
                binding.etNicName.showDropDown()
            }else{
                // clickOnNickNameList()
            }

        }

        binding.etNicName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nickName = s.toString().trim()


                // Check if NickName is empty

                if(nickName.isEmpty()){
                    binding.tilPurchasePartyNew.visibility=View.GONE
                    binding.tilPurchaseParty.visibility=View.VISIBLE
                }else{
                    if (isNichNameSelect==false) {
                        binding.tilPurchasePartyNew.visibility=View.GONE
                        binding.tilPurchaseParty.visibility=View.VISIBLE
                    } else {
                        binding.tilPurchasePartyNew.visibility=View.VISIBLE
                        binding.tilPurchaseParty.visibility=View.GONE
                        // Filter based on NickName

                    }
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        val parcelQtyEditText = binding.etParcelQty

// Set input filter to restrict values from 0–100 and block spaces
        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val newInput = dest.toString().substring(0, dstart) +
                    source.subSequence(start, end) +
                    dest.toString().substring(dend)

            // Block space character
            if (source == " ") return@InputFilter ""

            // Allow empty input (in case user is deleting)
            if (newInput.isEmpty()) return@InputFilter null

            // Check number range
            val value = newInput.toIntOrNull()
            return@InputFilter if (value != null && value in 0..999) {
                null // Acceptable input
            } else {
                "" // Reject input
            }
        }

// Apply the filter (append if there are existing filters)
        parcelQtyEditText.filters = arrayOf(inputFilter)

        val courierNoEditText = binding.etBiltyCourierNo
        val courierNoLayout = binding.tilBiltyCourierNo

// InputFilter to allow only alphanumeric characters
        val alphaNumericFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter null // Allow deletions
            val regex = Regex("^[a-zA-Z0-9]+$")
            if (source.matches(regex)) null else ""
        }

// Set both maxLength and alphanumeric filter
        courierNoEditText.filters = arrayOf(
            alphaNumericFilter,
            InputFilter.LengthFilter(20)
        )

// Optional: Show error if input contains invalid chars (only when not empty)
        courierNoEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.isNotEmpty() && !input.matches(Regex("^[a-zA-Z0-9]*$"))) {
                    courierNoLayout.error = "Only alphanumeric characters allowed"
                } else {
                    courierNoLayout.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        )

    }

    fun List<PurchasePartyData>.filterNonEmptyFields(): List<PurchasePartyData> {
        return this.filter { item ->
            item.id?.isNotEmpty() == true &&
                    item.nickName?.isNotEmpty() == true &&
                    item.accountName?.isNotEmpty() == true &&
                    item.nickNameStatus != null &&
                    item.fontColor?.isNotEmpty() == true
        }




    }

    private fun openAddBottomSheet(index: Int = -1) {
        addItemViewModel.bottomSheetIndex = index
        val bottomSheet = AddItemBottomSheetFragment.newInstance(true)
        AddItemBottomSheetFragment.newInstance(true)
            .show(childFragmentManager, AddItemBottomSheetFragment::class.simpleName)
    }

    private fun parchasePartyListByNickNameNew(nickNameId: String?) {
        isNickNameSelected=true
        viewModel.getPurchasePartyByNickName(nickNameId)


        viewModel.purchasePartyByNickName.observe(viewLifecycleOwner) {
            purchasePartyDataWithNicName = it
            if(purchasePartyDataWithNicName!!.isNotEmpty()){

                if(purchasePartyDataWithNicName!!.size==1){

                   /* addItemViewModel.clearWidgetList()
                    addItemViewModel.listDataChanged()
                    viewModel.addImageDataList.clear()
                    addItemViewModel.packTypeDataList= emptyList()

                    if (viewModel.addImageDataList.size > 0) {
                        binding.tvAddItem.isEnabled = false
                        binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
                    } else {
                        binding.tvAddItem.isEnabled = true
                        binding.tvAddItem.alpha = 1.0f
                    }*/
                    binding.etPurchasePartyNew.clearFocus()
                    binding.etPurchasePartyNew.hideKeyBoard()
                    binding.etPurchasePartyNew.setText(purchasePartyDataWithNicName!!.get(0).accountName)
                    purchasePartyId = purchasePartyDataWithNicName!!.get(0).id.toString()
                    addItemViewModel.getItemsListGr(purchasePartyId)

                }else{
                    binding.etPurchasePartyNew.setText("")
                }
                if (!::purchasePartyAdapterWithNickName.isInitialized) {

                    purchasePartyAdapterWithNickName = PurchasePartyAdapter(
                        false,
                        requireContext(),
                        R.layout.item_saleparty,
                        purchasePartyDataWithNicName.orEmpty()
                    )

                    binding.etPurchasePartyNew.threshold = 1
                    binding.etPurchasePartyNew.setAdapter(purchasePartyAdapterWithNickName)
                } else {
                    // Update adapter data if already initialized
                    purchasePartyAdapterWithNickName.updateData(purchasePartyDataWithNicName.orEmpty())
                }

                binding.etPurchasePartyNew.setOnItemClickListener { parent, v, position, l ->

                    // val purPartyItem = parent.getItemAtPosition(position)  as PurchasePartyData
                    val purPartyItem = parent.getItemAtPosition(position) as PurchasePartyData

                    Log.i("TaG", "selected purchase party --------->${purPartyItem}")

                /*    addItemViewModel.clearWidgetList()
                    addItemViewModel.listDataChanged()
                    viewModel.addImageDataList.clear()
                    addItemViewModel.packTypeDataList= emptyList()
                    if (viewModel.addImageDataList.size > 0) {
                        binding.tvAddItem.isEnabled = false
                        binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
                    } else {
                        binding.tvAddItem.isEnabled = true
                        binding.tvAddItem.alpha = 1.0f
                    }*/

                    binding.etPurchasePartyNew.setText(purPartyItem.accountName)
                    binding.etPurchasePartyNew.clearFocus()
                    binding.etPurchasePartyNew.hideKeyBoard()
                    binding.tilPurchasePartyNew.isErrorEnabled =
                        !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchasePartyNew.isErrorEnabled)

                    purchasePartyId = purPartyItem.id.toString()
                    addItemViewModel.getItemsListGr(purchasePartyId)

                }
            }else{
                Toast.makeText(context, "No Supiler found this nick Name", Toast.LENGTH_SHORT).show()


            }
            //    purchasePartyData = it



        }
    }

    private fun successOrderDialog(title: String, msg: String) {
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE).apply {
            titleText = title
            contentText = msg
            setCancelable(false)
            confirmText = getString(R.string.ok)
            confirmButtonBackgroundColor = getColor(context, R.color.red_2)
            setConfirmClickListener {
                println("ADD_ORDER_FRAGMENT_TO_DASHBOARD ${viewModel.pendingOrderID}")
                if (viewModel.pendingOrderID.isNotNullOrBlank()) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.addOrderFragment, true)
                        .build()
                    println("ADD_ORDER_FRAGMENT_TO_DASHBOARD 0 ${viewModel.pendingOrderID}")
                    findNavController().navigate(R.id.dashboardFragment, null, navOptions)
                }
                it.dismissWithAnimation()
            }

        }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        viewModel.isOrderPlacedSuccess.observe(viewLifecycleOwner) {
            println("MY_SUCCESS_MESSAGE $it")
            if (viewModel.pendingOrderID.isNotNullOrBlank())
                successOrderDialog(
                    "Update Confirmed",
                    it
                )
            else successOrderDialog("Record Created", it)

        }

        viewModel.isOrderPlaced.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.placeOrder.isEnabled=true
                binding.etCustomerCode.text.clear()
                binding.etBiltyCourierNo.text!!.clear()
                binding.etParcelQty.text!!.clear()
                binding.etSubParty.text.clear()
                binding.etDispatchType.text.clear()
                binding.etAverageDays.text?.clear()
                binding.etAvailableLimit.text?.clear()
                binding.etNicName.text?.clear()
                binding.etPurchaseParty.text?.clear()
                binding.etTransport.text.clear()
                binding.etStation.text.clear()
                binding.rgStartype.check(R.id.star1)
                binding.tilPVTMarka.visibility=View.GONE
                pvtMarka = "*"
                isPvtMarka=false
                binding.etScheme.text.clear()
                binding.etPurchaseParty.text.clear()
                binding.etDiscription.text?.clear()
                binding.etpvtMarka.text?.clear()

                binding.tvDispatchFromDate.text = ""
                binding.tvDispatchToDate.text = ""
                addItemViewModel.clearWidgetList()
                if (viewModel.addImageDataList.size > 0) {
                    binding.tvAddItem.isEnabled = false
                    binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
                } else {
                    binding.tvAddItem.isEnabled = true
                    binding.tvAddItem.alpha = 1.0f
                }
                addItemViewModel.listDataChanged()
                viewModel.addImageDataList.clear()
                addItemViewModel.packTypeDataList= emptyList()
                salePartyId = ""
                binding.llImageCount.visibility = View.GONE
                binding.etSubPartyRemark.text.clear()
                binding.autoCompleteStatus.setText("PENDING", false)
                addItemViewModel.packTypeDataList = emptyList()
                viewModel.addItemDataList.clear()


                subPartyData = emptyList()

                stationData = emptyList()
                transportData.clear()
                /*purchasePartyData       = emptyList()
                schemeData              = emptyList()*/

                /*     subPartyAdapter.setSubPartyData(subPartyData!!)
                     stationAdapter.setdefStationList(stationData!!)
                     defaultTransportAdapter.setDefTransportList(transportData!!) */

                /*purchasePartyAdapter.setPurchasePartyList(purchasePartyData!!)
                //check
                schemeAdapter.setSchemeData(schemeData!!)*/

                viewModel.getVoucher()
            }
        }
        viewModel.isOrderPlacedLimitError.observe(viewLifecycleOwner) { errorMsg ->
            binding.placeOrder.isEnabled=true
            requireContext().showWarningDialog(
                getString(R.string.confirm_order), // need confirmation on this
                errorMsg,
                confirmText = getString(R.string.ok),
                cancelText = getString(cn.pedant.SweetAlert.R.string.dialog_cancel)
            ) { dialog ->

                if (validate()) binding.apply {
                    var totalQty = 0
                    var totalAmount = 0

                    addItemViewModel.packTypeDataList.forEach {
                        if (it.amount.isNotNullOrBlank() && it.qty.isNotNullOrBlank()) {
                            totalAmount += (it.amount ?: "0").toInt()
                            totalQty += (it.qty ?: "0").toInt()
                        }

                    }
                    val hashMap: HashMap<String, RequestBody?> = HashMap()
                    hashMap["SalePartyId"] = salePartyId.toRequestBody()

                    if (subPartyId == "00000000-0000-0000-0000-000000000000") {
                        hashMap["SubPartyId"] = "".toRequestBody()
                    } else {

                        if(subPartyId.equals("null")){
                            hashMap["SubPartyId"] = "".toRequestBody()
                        }else{
                            hashMap["SubPartyId"] = subPartyId.toRequestBody()
                        }

                    }

                    if(isSubPartyRemark){
                        hashMap["SubPartyId"] = "".toRequestBody()
                        hashMap["TransportId"] = transportId.toRequestBody()
                        hashMap["BstationId"] = bookingStationId.toRequestBody()
                    }else{
                        hashMap["TransportId"] = transportId.toRequestBody()
                        hashMap["BstationId"] = bookingStationId.toRequestBody()
                    }


                    //  hashMap["SubPartyId"] = subPartyId.toRequestBody()
                    hashMap["SubPartyasRemark"] = etSubPartyRemark.text.toString().toRequestBody()
                    hashMap["SupplierId"] = purchasePartyId.toRequestBody()
                    // hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()

                    hashMap["DispatchTypeID"] = dispatchId.toRequestBody()

                    hashMap["SchemeId"] = schemeId.toRequestBody()
                    hashMap["OrderCategary"] = pvtMarka.toRequestBody()
                    hashMap["DeliveryDateFrom"] = tvDispatchFromDate.text.toString().toRequestBody()
                    hashMap["DeliveryDateTo"] = tvDispatchToDate.text.toString().toRequestBody()
                    hashMap["Remark"] = etDiscription.text.toString().toRequestBody()
                    if (isPvtMarka){
                        hashMap["PvtMarka"] = etpvtMarka.text.toString().toRequestBody()
                    }
                    hashMap["TotalQty"] = "0".toRequestBody()
                    hashMap["TotalAmt"] = "0".toRequestBody()
                    hashMap["OrderTypeName"] = "TRADING".toRequestBody()
                    hashMap["OrderStatus"] = "HOLD".toRequestBody()
                    hashMap["TraceIdentifier"] = traceIdentifier?.toRequestBody()
                    if (viewModel.pendingOrderID.isNotNullOrBlank()) hashMap["id"] =
                        (editData?.id ?: "").toRequestBody()

                    println("PLACING_ORDER 1 ${Gson().toJson(hashMap)}")

                    viewModel.placeOrder(hashMap)

                }
                binding.placeOrder.isEnabled=false
                dialog.dismissWithAnimation()
            }
        }
        viewModel.setEditOrderFields.observe(viewLifecycleOwner) {
            binding.placeOrder.text = "Update"
            val pendingOrderId = activity?.intent?.getStringExtra(ARG_PENDING_ORDER_ID)
            viewModel.getEditOrderDataIfNeededGr(pendingOrderId!!)
        }


        viewModel.editOrderDataGr.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                try {

                    editDataGr = it

                    println("GETTING_EDIT_DATA $editData")

                    /*   if (it == true) {
                           statusDropDownAdapter = ArrayAdapter(
                               requireContext(),
                               android.R.layout.simple_dropdown_item_1line,
                               statusOptionsOnEdit
                           )
                           binding.autoCompleteStatus.setAdapter(statusDropDownAdapter)
                       } else {
                           statusDropDownAdapter = ArrayAdapter(
                               requireContext(),
                               android.R.layout.simple_dropdown_item_1line,
                               statusOptions
                           )
                           binding.autoCompleteStatus.setAdapter(statusDropDownAdapter)
                       }*/


                    //     binding.etSerialNo.setText(serialNo)
                    //    binding.etMarketerCode.setText(voucherNo)

                    var job1: Deferred<Job>? = null


                    if (salePartyData.isNullOrEmpty()) {
                        salePartyData = listOf(
                            SalepartyData(
                                accountID = it?.customerId,
                                accountName = it?.customerName,
                                rno = ""
                            )
                        )
                        if (::salePartyAdapter.isInitialized) {
                            salePartyAdapter.setItem(salePartyData)
                            Log.i(
                                "TaG",
                                "make new data 1------------=-===========--->${salePartyData}"
                            )
                        }
                    } else {
                        if (salePartyData?.any { its -> its.accountID == it?.customerId } == false) {
                            val tempList: MutableList<SalepartyData> = mutableListOf()
                            tempList.addAll(salePartyData ?: emptyList())
                            tempList.addAll(
                                listOf(
                                    SalepartyData(
                                        accountID = it?.customerId,
                                        accountName = it?.customerName,
                                        rno = ""
                                    )
                                )
                            )
                            salePartyData = tempList
                            if (::salePartyAdapter.isInitialized) {
                                salePartyAdapter.setItem(salePartyData)
                                Log.i(
                                    "TaG",
                                    "make new data 2------------=-===========--->${salePartyData}"
                                )
                            }
                        }
                    }

                    val editSaleParty =
                        salePartyData?.filter { saleData -> saleData.accountID == it?.customerName }
                    if (!editSaleParty.isNullOrEmpty()) {
                        binding.etCustomerCode.setText(editSaleParty[0].accountName)
                        salePartyId = editSaleParty[0].accountID ?: ""
                        job1 = async {
                            viewModel.getSalePartyAndStationData(
                                salePartyId,
                                salePartyId,
                                ""
                            )
                        }

                    }

                    job1?.join()







                    //    binding.etDiscription.setText(it?.remark)
                    binding.rgPurchaseParty.check(R.id.radioBySupplierName)
                    binding.etTransport.setText(it?.transportName)
                    binding.etCustomerCode.setText(it?.customerName)
                    binding.etSubParty.setText(it?.subPartyName)


                    binding.etBiltyCourierNo.setText(it?.courierNo)
                    val qty = it?.parcelQty ?: 0.0

                    if (qty < 999) {
                        val formattedQty = if (qty % 1 == 0.0) {
                            qty.toInt().toString()  // 43.00 → "43"
                        } else {
                            String.format("%.2f", qty)  // If needed, 43.25 → "43.25"
                        }

                        // Only set if formatted length ≤ 3
                        if (formattedQty.length <= 3) {
                            binding.etParcelQty.setText(formattedQty)
                        } else {
                            // Optional: truncate or notify user
                            binding.etParcelQty.setText(formattedQty.take(3))
                        }
                    }

                    transportId=it?.transportId!!
                    transportIDdMainBranch=it?.transportId!!
                    subPartyId = it.subPartyId.toString()
                    println("GETTING_TRANSPORT_DATA ${transportData}")
                    binding.etTransport.setText(it.transportName)


                    transportId = it.transportId

                    binding.etPurchaseParty.setText(it.supplierName)

                    salePartyId=it.supplierId.toString()
                    // for testing
                    val imageUrlList = mutableListOf<String>()

                    it?.itemDetailsImageList
                        ?.filter { image ->
                            image.documentType == "GRIN" && !(image.imagePath?.contains("pdf", ignoreCase = true) ?: false)
                        }
                        ?.forEach { image ->
                            image.imagePath?.let { path -> imageUrlList.add(path) }
                        }


                    // val imageUrlList = arrayOf("https://image.ssspltd.com/sybererp/IMAGES/43029624-ea4a-434c-9a14-d7da24840bad/Screenshot_20241108-211432_Instagram20241111161911939.jpg")
                    val imageUriList = imageUrlList.map { its -> Uri.parse(its) }
                    SharedEditImageUriList.imageUris = imageUriList

                    if (imageUriList.isNotEmpty()) {
                        binding.llImageCount.visibility = View.VISIBLE
                        binding.tvImageCount.text = checkTwoDigitNo(imageUriList.size.toString())
                    }


                    val tempItemDetailList = mutableListOf<PackType>()
                    it?.goodsReturnItemList?.forEach { itemDetail ->

                        val tempSubItemList = mutableListOf<PackTypeItem>()
                        tempSubItemList.add(
                            PackTypeItem(
                                itemID = itemDetail?.itemId,
                                itemName = itemDetail?.itemName,
                                itemQuantity = (itemDetail?.itemQty ?: 0).toInt().toString()
                            )
                        )


                        tempItemDetailList.add(
                            PackType(
                                id = itemDetail?.id ?: "",
                                pcsId = itemDetail?.itemId ?: "",
                                packName = itemDetail?.itemName,
                                qty = itemDetail?.itemQty.toString(),
                                amount = "",
                                itemDetail = tempSubItemList.toList()
                            )
                        )
                    }


                    viewModel.addItemDataList = ArrayList(tempItemDetailList)
                    addItemViewModel.packTypeDataList = tempItemDetailList
                    addItemViewModel.getPackDataList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        viewModel.voucherData.observe(viewLifecycleOwner) {
            traceIdentifier = it?.traceIdentifier ?: ""
            if (it?.voucherCode.isNullOrBlank()) {
                showAlertMsg(
                    AlertMsg(
                        "Error",
                        "Voucher code not configured!",
                        SweetAlertDialog.ERROR_TYPE,
                        false,
                        "OK",
                        barColor = R.color.color_A5DC86,
                        btnBgColor = R.color.error_text
                    )
                )
            }

            // binding.etMarketerCode.setText(it?.voucherCode)
            binding.etSerialNo.setText(it?.voucherNO)

            if (it?.isVisible == false) {
                binding.tilRadioStar.visibility = View.GONE

            } else {
                binding.tilRadioStar.visibility = View.VISIBLE
            }
        }


        viewModel.customerDataResp.observe(viewLifecycleOwner) { customerList ->

            val salePartyList = customerList.map { it.toSalepartyData() }

            salePartyData = salePartyList
            salePartyAdapter = SalePartyAdapter(requireContext(), R.layout.item_saleparty, salePartyList)

            binding.etCustomerCode.threshold = 1
            binding.etCustomerCode.setAdapter(salePartyAdapter)

            binding.etCustomerCode.setOnItemClickListener { parent, _, position, _ ->
                val salePartyItem = salePartyAdapter.getItem(position)
                salePartyId = salePartyItem?.accountID.orEmpty()
                //    viewModel.getSalePartyAndStationData(salePartyId, salePartyId, "")
                viewModel.getSubPartyGR(salePartyId)

                binding.tilSaletParty.isErrorEnabled =
                    !(salePartyItem?.accountName.isNotNullOrBlank() || binding.tilSaletParty.isErrorEnabled)
            }
        }

        //Response Subparty GR
        viewModel.subPartyDetailGR.observe(viewLifecycleOwner) {
            val apiResponseNew = it?: return@observe


            if (viewModel.pendingOrderID.isNotNullOrBlank()){

            }
            else{
                // Ensure subPartyList is not null or empty
                if (!apiResponseNew.isNullOrEmpty()) {
                    // Set subpart data
                    val subPartyList = apiResponseNew.toSubPartyList().reversed()

                    subPartyId=""
                    binding.etSubParty.setText("SELF")
                    binding.tilSubParty.isErrorEnabled = false
                    binding.tilSubParty.error = null

                   /* subPartyId = apiResponseNew[0].accountId.toString()
                    binding.tilSubParty.isErrorEnabled =
                        !(subPartyList.get(0).subPartyName.isNotNullOrBlank() || binding.tilSubParty.isErrorEnabled)
*/
                    subPartyAdapter = SubPartyAdapter(
                        requireContext(), R.layout.item_saleparty, subPartyList
                    )
                 //   binding.etSubParty.setText(subPartyList.get(0).subPartyName)
                    subPartyData = subPartyList
                    binding.etSubParty.threshold = 1
                    binding.etSubParty.setAdapter(subPartyAdapter)


                    binding.etSubParty.setOnItemClickListener { parent, _, position, _ ->
                        val subPartyItem = subPartyAdapter.getItem(position) ?: return@setOnItemClickListener
                        subPartyId = subPartyItem.subPartyId


                        binding.tilSubParty.isErrorEnabled =
                            !(subPartyItem?.subPartyName.isNotNullOrBlank() || binding.tilSubParty.isErrorEnabled)
                    }
                }
                else {
                    // Handle case where subPartyList is null or empty
                    subPartyId = ""
                    transportId = ""
                    bookingStationId = ""
                    binding.etSubParty.text.clear()
                    binding.etTransport.text.clear()
                    binding.etStation.text.clear()
                }
            }
        }

        viewModel.transPortDetailGR.observe(viewLifecycleOwner) {
            val apiResponseNew = it?: return@observe
            // Set transport data
            if (apiResponseNew.isNotEmpty()) {
                // Convert to List<DefTransport>
                val transportData = apiResponseNew.toDefTransportList()

                // Set transport data
                if (transportData.isNotEmpty()) {
                    transportDataWithRemark?.clear()
                    transportDataWithRemark?.addAll(transportData)

                    transportDataWithSubparty?.clear()
                    transportDataWithSubparty?.addAll(transportData)

                    defaultTransportAdapter = DefaultTransportAdapter(
                        requireContext(),
                        R.layout.item_saleparty,
                        transportData
                    )

                    binding.etTransport.threshold = 1
                    binding.etTransport.setAdapter(defaultTransportAdapter)
                }

                binding.etTransport.setOnItemClickListener { _, _, position, _ ->
                    val transportItem = defaultTransportAdapter.getItem(position) ?: return@setOnItemClickListener

                    if(defaultTransportAdapter.getItem(position)!!.defaultStatus){
                        transportId = transportItem.transportId
                        isTransPort=true
                    }else{
                        courierId = transportItem.transportId
                        isTransPort=false
                    }



                    binding.tilTransport.isErrorEnabled =
                        !(transportItem?.transportName.isNotNullOrBlank() || binding.tilSubParty.isErrorEnabled)
                }

                val firstTransport = transportData!![0]

                transportId = firstTransport.transportId
                transportIDdMainBranch = firstTransport.transportId
            }else{
                transportData.clear()
            }

        }


        viewModel.salePartyDetail.observe(viewLifecycleOwner) {
            val apiResponseNew = it ?: return@observe


            if (viewModel.pendingOrderID.isNotNullOrBlank()){

            }
            else{
                // Ensure subPartyList is not null or empty
                if (!apiResponseNew.subPartyList.isNullOrEmpty()) {
                    // Set subpart data
                    val firstSubParty = apiResponseNew.subPartyList[0]
                    transportData = firstSubParty.transportList.toMutableList()
                    subPartyId = firstSubParty.subPartyId

                    subPartyAdapter = SubPartyAdapter(
                        requireContext(), R.layout.item_saleparty, apiResponseNew.subPartyList
                    )
                    subPartyData = apiResponseNew.subPartyList
                    binding.etSubParty.threshold = 1
                    binding.etSubParty.setAdapter(subPartyAdapter)


                    binding.etSubParty.setOnItemClickListener { parent, _, position, _ ->
                        val subPartyItem = subPartyAdapter.getItem(position) ?: return@setOnItemClickListener
                        subPartyId = subPartyItem.subPartyId

                        val selectedTransportList = subPartyItem.transportList ?: emptyList()

                        if (selectedTransportList.isNotEmpty()) {
                            transportData= selectedTransportList.toMutableList();
                            val firstTransport = selectedTransportList[0]
                            val stationList = firstTransport.stationList ?: emptyList()

                            stationAdapter = StationAdapter(
                                requireContext(), R.layout.item_saleparty, stationList
                            )
                            stationData = firstTransport.stationList

                            binding.etStation.threshold = 1
                            binding.etStation.setAdapter(stationAdapter)
                            stationAdapter.setdefStationList(stationList)
                            stationAdapter.notifyDataSetChanged()

                            if (stationList.isNotEmpty()) {
                                binding.etStation.setText(stationList[0].stationName)
                                bookingStationId = stationList[0].stationId
                            } else {
                                bookingStationId = ""
                                binding.etStation.text.clear()
                            }

                            binding.etTransport.setText(firstTransport.transportName)
                            transportId = firstTransport.transportId
                        }
                        else {
                            transportData.clear()
                            stationData= emptyList()
                            //   stationAdapter.notifyDataSetChanged()
                            transportId = ""
                            binding.etTransport.text.clear()
                            bookingStationId = ""
                            binding.etStation.text.clear()
                        }

                        defaultTransportAdapter = DefaultTransportAdapter(
                            requireContext(), R.layout.item_saleparty, selectedTransportList
                        )
                        binding.etTransport.threshold = 1
                        binding.etTransport.setAdapter(defaultTransportAdapter)

                        binding.etTransport.setOnItemClickListener { _, _, transportPosition, _ ->
                            val transportItem = defaultTransportAdapter.getItem(transportPosition) ?: return@setOnItemClickListener
                            transportId = transportItem.transportId

                            val transportStations = transportItem.stationList ?: emptyList()
                            stationAdapter = StationAdapter(
                                requireContext(), R.layout.item_saleparty, transportStations
                            )
                            stationData = transportStations
                            binding.etStation.threshold = 1
                            binding.etStation.setAdapter(stationAdapter)
                            stationAdapter.setdefStationList(transportStations)
                            stationAdapter.notifyDataSetChanged()

                            binding.etStation.setOnItemClickListener { parent, _, position, _ ->
                                val stationId = stationAdapter.getItem(position)

                                binding.tilStation.isErrorEnabled =
                                    !(stationId?.stationId.isNotNullOrBlank() || binding.tilStation.isErrorEnabled)
                                bookingStationId = stationId?.stationId.toString()
                            }
                            if (transportStations.isNotEmpty()) {
                                binding.etStation.setText(transportStations[0].stationName, false)
                                bookingStationId = transportStations[0].stationId
                            } else {
                                bookingStationId = ""
                                binding.etStation.text.clear()
                            }
                        }


                        stationAdapter = StationAdapter(
                            requireContext(), R.layout.item_saleparty, stationData!!
                        )
                        stationData = stationData
                        binding.etStation.threshold = 1
                        binding.etStation.setAdapter(stationAdapter)
                        stationAdapter.setdefStationList(stationData!!)
                        stationAdapter.notifyDataSetChanged()
                    }


                    binding.etSubParty.setText(apiResponseNew.defSubPartyName, false)

                    subPartyId = firstSubParty.subPartyId

                    subPartyIdMainBranch=firstSubParty.subPartyId

                    // Set transport data
                    if (!transportData.isNullOrEmpty()) {
                        transportDataWithRemark!!.clear()
                        transportDataWithRemark!!.addAll(transportData!!)
                        transportDataWithSubparty!!.clear()
                        transportDataWithSubparty!!.addAll(transportData!!)
                        defaultTransportAdapter = DefaultTransportAdapter(
                            requireContext(), R.layout.item_saleparty, transportData!!
                        )
                        binding.etTransport.threshold = 1
                        binding.etTransport.setAdapter(defaultTransportAdapter)

                        binding.etTransport.setOnItemClickListener { _, _, position, _ ->
                            val transportItem = defaultTransportAdapter.getItem(position) ?: return@setOnItemClickListener
                            transportId = transportItem.transportId

                            val stationList = transportItem.stationList ?: emptyList()
                            stationAdapter = StationAdapter(
                                requireContext(), R.layout.item_saleparty, stationList
                            )
                            stationData = stationList
                            binding.etStation.threshold = 1
                            binding.etStation.setAdapter(stationAdapter)
                            stationAdapter.setdefStationList(stationList)
                            stationAdapter.notifyDataSetChanged()

                            if (stationList.isNotEmpty()) {
                                binding.etStation.setText(stationList[0].stationName, false)
                                bookingStationId = stationList[0].stationId
                            } else {
                                transportData.clear()
                                bookingStationId = ""
                                binding.etStation.text.clear()
                            }
                        }

                        val firstTransport = transportData!![0]
                        binding.etTransport.setText(firstTransport.transportName, false)

                        transportId = firstTransport.transportId
                        transportIDdMainBranch = firstTransport.transportId

                        val firstStationList = firstTransport.stationList ?: emptyList()
                        if (firstStationList.isNotEmpty()) {

                            stationDataWithRemark!!.clear()
                            stationDataWithRemark!!.addAll(stationData!!)
                            stationDataWithSubparty!!.clear()
                            stationDataWithSubparty!!.addAll(stationData!!)
                            stationData = firstStationList
                            stationAdapter = StationAdapter(
                                requireContext(), R.layout.item_saleparty, firstStationList
                            )
                            binding.etStation.threshold = 1
                            binding.etStation.setAdapter(stationAdapter)
                            stationAdapter.setdefStationList(firstStationList)
                            stationAdapter.notifyDataSetChanged()
                            binding.etStation.setText(firstStationList[0].stationName,true)
                            bookingStationId = firstStationList[0].stationId
                            stationIDdMainBranch = firstStationList[0].stationId

                            binding.etStation.setOnItemClickListener { parent, _, position, _ ->
                                val stationId = stationAdapter.getItem(position)
                                binding.tilStation.isErrorEnabled =
                                    !(stationId?.stationId.isNotNullOrBlank() || binding.tilStation.isErrorEnabled)
                                bookingStationId = stationId?.stationId.toString()
                            }
                        } else {
                            stationData = emptyList()
                            bookingStationId = ""
                            binding.etStation.text.clear()
                        }
                    }else{

                        transportData.clear()
                    }

                }
                else {
                    // Handle case where subPartyList is null or empty
                    subPartyId = ""
                    transportId = ""
                    bookingStationId = ""
                    binding.etSubParty.text.clear()
                    binding.etTransport.text.clear()
                    binding.etStation.text.clear()
                }
            }

            var avlLimit = "0"
            if (apiResponseNew.avlLimit != null && apiResponseNew!!.avlLimit.toDouble() != 0.0) {
                avlLimit = apiResponseNew.avlLimit.toString()
            } else {
                binding.autoCompleteStatus.setText("PENDING", false)
            }

            binding.etAvailableLimit.setTextColor(
                getColor(requireContext(), if (avlLimit.contains("-") || avlLimit == "0") R.color.error_text else R.color.green)
            )
            binding.etAvailableLimit.setText(avlLimit)

            binding.etAverageDays.setText(apiResponseNew.avgDays.toString())
        }

        viewModel.purchaseParty_.observe(viewLifecycleOwner) {
            purchasePartyData = it?.filterNonEmptyFields()
            isNickNameSelected=false
            //supiler list
            if (!::purchasePartyAdapter.isInitialized) {

                purchasePartyAdapter = PurchasePartyAdapter(
                    isNickNameSelected,
                    requireContext(),
                    R.layout.item_saleparty,
                    purchasePartyData.orEmpty()
                )

                binding.etPurchaseParty.threshold = 1
                binding.etPurchaseParty.setAdapter(purchasePartyAdapter)

            }
            else {
                if (isNickNameSelected) {
                    purchasePartyAdapter.updateType(true)
                    purchasePartyAdapter.updateData(purchasePartyData.orEmpty())
                } else {
                    purchasePartyAdapter.updateType(false)
                    purchasePartyAdapter.updateData(purchasePartyData.orEmpty())
                }
            }

            binding.etPurchaseParty.setOnItemClickListener { parent, v, position, l ->

                isNichNameSelect=false

                // val purPartyItem = parent.getItemAtPosition(position)  as PurchasePartyData
                val purPartyItem = parent.getItemAtPosition(position) as PurchasePartyData

                Log.i("TaG", "selected purchase party --------->${purPartyItem}")
                binding.etPurchaseParty.setText(purPartyItem.accountName.toString())
                binding.etPurchaseParty.clearFocus()
                binding.etPurchaseParty.hideKeyBoard()
                binding.tilPurchaseParty.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchaseParty.isErrorEnabled)
                purchasePartyId = purPartyItem.id.toString()
                addItemViewModel.getItemsListGr(purchasePartyId)

                binding.etNicName.setText(purPartyItem.nickName.toString())
                binding.etNicName.clearFocus()
                binding.etNicName.hideKeyBoard()
                binding.tilNickNameList.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilNickNameList.isErrorEnabled)


            }

            if (viewModel.pendingOrderID.isNotNullOrBlank()) {

            }else{
                if (!binding.etPurchaseParty.isPopupShowing) {
                    binding.etPurchaseParty.showDropDown()
                }
            }



        }

        viewModel.scheme.observe(viewLifecycleOwner) { list ->

            val activeList = list.orEmpty().filter { it.activeStatus }

            schemeAdapter = SchemeAdapter(
                requireContext(),
                R.layout.item_saleparty,
                activeList
            )

            schemeData = activeList

            binding.etScheme.threshold = 1
            binding.etScheme.setAdapter(schemeAdapter)

            binding.etScheme.setOnItemClickListener { parent, _, position, _ ->

                val schemeItem = schemeAdapter.getItem(position)

                schemeId = schemeItem?.schemeId.toString()

                binding.etPurchaseParty.text.clear()

                viewModel.getNickNameList(null, schemeItem?.schemeId, true)

                binding.tilScheme.isErrorEnabled =
                    !(schemeItem?.schemeId.isNullOrBlank())
            }
        }


        addItemViewModel.isListAvailable.observe(viewLifecycleOwner) {
            addItemAdapter.submitList(addItemViewModel.widgetList)

            if (addItemViewModel.widgetList.size > 0) {
                binding.tvAddItem.isEnabled = false
                binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
            } else {
                binding.tvAddItem.isEnabled = true
                binding.tvAddItem.alpha = 1.0f
            }
            Log.i(
                "TaG",
                "packTypeDataList Set 2222-=-------->> ${addItemViewModel.packTypeDataList}"
            )
            viewModel.addItemDataList = ArrayList(addItemViewModel.packTypeDataList)
            binding.tvAddItem.requestFocus()
            binding.tvAddItem.setBackgroundResource(R.drawable.gray_300_bg)
            binding.tvAddItem.error = null
        }


        apiResponseDispatchType()
    }


    fun List<TransportMasterData>.toDefTransportList(): List<DefTransport> {
        return this.mapNotNull {
            if (it.id != null && it.transportName != null) {
                DefTransport(
                    transportId = it.id,
                    transportName = it.transportName,
                    defaultStatus = it.isTransport ?: false,
                    stationList = emptyList() // No station data in this API, leave empty
                )
            } else null
        }
    }
    fun List<SubPartyGRData>.toSubPartyList(): List<SubParty> {
        return this
            .filter { !it.accountName.isNullOrBlank() && !it.accountId.isNullOrBlank() }
            .groupBy { it.accountName!! to it.accountId!! } // Group by both
            .map { (nameAndId, groupByAccount) ->

                val (accountName, accountId) = nameAndId

                val defTransportList = groupByAccount
                    .filter { !it.transportId.isNullOrBlank() }
                    .groupBy { it.transportId!! }
                    .map { (transportId, groupByTransport) ->

                        val transportName = groupByTransport.first().transportName ?: ""

                        val stations = groupByTransport.mapNotNull {
                            if (!it.stationID.isNullOrBlank() && !it.stationName.isNullOrBlank()) {
                                AllStation(
                                    stationId = it.stationID!!,
                                    stationName = it.stationName!!
                                )
                            } else null
                        }

                        DefTransport(
                            transportId = transportId,
                            transportName = transportName,
                            defaultStatus = false,
                            stationList = stations
                        )
                    }

                SubParty(
                    subPartyId = accountId,
                    subPartyName = accountName,
                    mobileNo = "",
                    transportList = defTransportList
                )
            }
    }

    fun CustomerData.toSalepartyData(): SalepartyData {
        return SalepartyData(
            accountID = this.id,
            accountName = this.accountName,
            rno = null // or assign any field if needed
        )
    }

    private fun showAlertMsg(config: AlertMsg? = AlertMsg()) {
        SweetAlertDialog(requireActivity(), config?.type!!).apply {
            titleText = config.title
            contentText = config.message
            setCancelable(false)
            confirmText = config.okButtonText
            confirmButtonBackgroundColor = getColor(requireContext(), config.btnBgColor)
            setConfirmClickListener {
                dismiss()
                config.isOkCallBack?.let { it1 -> it1() }
            }
        }.show()
    }

    private fun validate(): Boolean = with(binding) {
        if (etCustomerCode.text.isNullOrBlank()) {
            etCustomerCode.requestFocus()
            tilSaletParty.isErrorEnabled = true
            tilSaletParty.setError("You need to select Cutomer code")
            return false
        }
        else if (etSubParty.text.isNullOrBlank())   {
            etSubParty.requestFocus()
            tilSubParty.isErrorEnabled = true
            tilSubParty.setError("You need to select sub party")
            return false
        } else if (etTransport.text.isBlank()) {
            etTransport.requestFocus()
            tilTransport.isErrorEnabled = true
            tilTransport.setError("You need to select transport")
            return false
        }
        else if(isNichNameSelect == true&&etPurchasePartyNew.text.isBlank()){
            etPurchasePartyNew.requestFocus()
            tilPurchasePartyNew.isErrorEnabled = true
            tilPurchasePartyNew.setError("You need to select purchase party")
            return false
        }
        else if(isNickNameSelected==false&& etPurchaseParty.text.isBlank()){
            etPurchaseParty.requestFocus()
            tilPurchaseParty.isErrorEnabled = true
            tilPurchaseParty.setError("You need to select purchase party")
            return false
        }
        else if (etParcelQty.text.toString().isBlank()) {
            etParcelQty.requestFocus()
            tilParcelQty.isErrorEnabled = true
            tilParcelQty.setError("You need to enter parcel Qty.")
            return false
        }
        else if (viewModel.addItemDataList.size == 0) {
            tvAddItem.requestFocus()
            tvAddItem.setBackgroundResource(com.ssspvtltd.quick.R.drawable.red_outline)
            tvAddItem.error = "You need to add some item"
            return false
        }
        return true
    }

    private val addItemResultLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val list = it.data?.getParcelableArrayListExt<PackType>(ARG_ADD_ITEM_LIST)
            viewModel.addItemDataList = list ?: arrayListOf()
        }
    }

    private val addImageResultLauncher = registerForActivityResult(StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val list = it.data?.getParcelableArrayListExt<ImageModel>(ARG_ADD_IMAGE_LIST)
            viewModel.addImageDataList = list ?: arrayListOf()
            if (viewModel.addImageDataList.size > 0) {
                binding.llImageCount.visibility = View.VISIBLE
                binding.tvImageCount.text =
                    checkTwoDigitNo(viewModel.addImageDataList.size.toString())
            } else {
                binding.llImageCount.visibility = View.GONE
            }
        }
    }

    private fun checkTwoDigitNo(no: String): String {
        if (no.length > 9) {
            return no
        }
        return "0$no"
    }
    private fun apiResponseDispatchType(){
        viewModel.getDispatchTypeList.observe(viewLifecycleOwner) {

            dispatchAdapter = DispatchAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            dispatchTypeData = it
            binding.etDispatchType.threshold = 1
            binding.etDispatchType.setAdapter(dispatchAdapter)
            binding.etDispatchType.setOnItemClickListener { parent, _, position, _ ->
                val schemeItem = dispatchAdapter.getItem(position)
                dispatchId = schemeItem.id

                binding.tilDispatchType.isErrorEnabled =
                    !(schemeItem.id.isNotNullOrBlank() || binding.tilDispatchType.isErrorEnabled)
            }


        }
    }
    private fun nickNameList(){
        if (isSchemeHasData) {
            viewModel.getInitialPurchaseParty(null,schemeId, true)
        } else {
            viewModel.getNickNameList(null,null, true)
        }
        if (::nickNameAdapter.isInitialized) nickNameAdapter.updateType(true)
        isNickNameSelected = true



        viewModel.nickNameList.observe(viewLifecycleOwner) {



            nicNaneData = it?.filterNonEmptyFields()
            //nick name list
            if (!::nickNameAdapter.isInitialized) {

                nickNameAdapter = PurchasePartyAdapter(
                    true,
                    requireContext(),
                    R.layout.item_saleparty,
                    nicNaneData.orEmpty()
                )

                binding.etNicName.threshold = 1
                binding.etNicName.setAdapter(nickNameAdapter)

            }
            else {
                nickNameAdapter.updateType(false)
                nickNameAdapter.updateData(nicNaneData.orEmpty())

            }


            binding.etNicName.setOnItemClickListener { parent, v, position, l ->

                isNichNameSelect=true

                // val purPartyItem = parent.getItemAtPosition(position)  as PurchasePartyData
                val purPartyItem = parent.getItemAtPosition(position) as PurchasePartyData

                Log.i("TaG", "selected purchase party --------->${purPartyItem}")
                binding.tilPurchasePartyNew.visibility=View.VISIBLE
                binding.tilPurchaseParty.visibility=View.GONE
                binding.etPurchasePartyNew.text?.clear()
                purchasePartyId = purPartyItem.nickNameId.toString()

               /* addItemViewModel.clearWidgetList()
                addItemViewModel.listDataChanged()
                viewModel.addImageDataList.clear()
                addItemViewModel.packTypeDataList= emptyList()
                if (viewModel.addImageDataList.size > 0) {
                    binding.tvAddItem.isEnabled = false
                    binding.tvAddItem.alpha = 0.5f  // Optional: visually indicate disabled
                } else {
                    binding.tvAddItem.isEnabled = true
                    binding.tvAddItem.alpha = 1.0f
                }*/

                //  parchasePartyListByNickName("8873CD90-A606-403D-B2A2-372580DA710B")

                binding.etNicName.setText(purPartyItem.nickName.toString())
                binding.etNicName.clearFocus()
                binding.etNicName.hideKeyBoard()
                binding.tilNickNameList.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilNickNameList.isErrorEnabled)

                parchasePartyListByNickNameNew(purchasePartyId)
                addItemViewModel.getItemsListGr(purPartyItem.nickNameId.toString())
             }




            /*  viewModel.getPurchaseParty(null,null, false)
              if (::purchasePartyAdapter.isInitialized) purchasePartyAdapter.updateType(false)
              isNickNameSelected = false*/

        }
    }


    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Exit")
            .setMessage("Are you sure you want to go back?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()// or navController.popBackStack()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }







}
