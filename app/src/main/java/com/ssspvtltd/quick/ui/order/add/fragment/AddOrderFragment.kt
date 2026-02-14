package com.ssspvtltd.quick.ui.order.add.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
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
import com.ssspvtltd.quick.databinding.FragmentAddOrderBinding
import com.ssspvtltd.quick.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick.model.ARG_ADD_ITEM_LIST
import com.ssspvtltd.quick.model.alert.AlertMsg
import com.ssspvtltd.quick.model.order.add.DispatchTypeList
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartyNewList.DefTransport
import com.ssspvtltd.quick.model.order.add.salepartyNewList.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.Data

import com.ssspvtltd.quick.model.order.add.salepartyNewList.SubParty
import com.ssspvtltd.quick.ui.order.add.activity.AddImageActivity
import com.ssspvtltd.quick.ui.order.add.adapter.DefaultTransportAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.DispatchAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.PackDataAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.PurchasePartyAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SalePartyAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SchemeAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.StationAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.SubPartyAdapter
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddOrderViewModel
import com.ssspvtltd.quick.utils.SharedEditImageUriList
import com.ssspvtltd.quick.utils.extension.getParcelableArrayListExt
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.isNotNullOrBlank
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.filter
import kotlin.collections.orEmpty


@AndroidEntryPoint
class AddOrderFragment

    : BaseFragment<FragmentAddOrderBinding, AddOrderViewModel>() {
    // private val addItemViewModel by activityViewModels<AddItemViewModel>()
    override val inflate: InflateF<FragmentAddOrderBinding> get() = FragmentAddOrderBinding::inflate
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
    private var isRgSubparty: Boolean = true
    private var bookingStationId: String = ""
    private var transportId: String = ""
    private var dispatchId: String = ""
    private var transportIdNew: String = ""
    private var schemeId: String = ""
    private var selectedStatus: String = "PENDING"
    private var mobileNumberPurchaseParty: String? = null
    private var mobileNumberSubParty: String? = null


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
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var isNickNameSelected = true
    private var isSchemeHasData = false
    private lateinit var salePartyAdapter: SalePartyAdapter
    private var editData: EditOrderDataNew? = null
    private val addItemAdapter by lazy { PackDataAdapter() }
    private var statusOptions = listOf("PENDING", "HOLD")
    private var statusOptionsOnEdit = listOf("PENDING", "HOLD", "CANCEL")
    private var isSubPartyRadioSelect = true
    private lateinit var statusDropDownAdapter: ArrayAdapter<String>
    private var traceIdentifier: String? = "00000000-0000-0000-0000-000000000000"
    private var IS_EDITABLE: Boolean? = false
    private var isNichNameSelect: Boolean? = false
    private lateinit var data: Data
    private var isApiCalled = false // Flag to track API call
    private var isEditOrder = false // Flag to track API call
    private var isSubPartyRemark = false
    private var eInvoiceStatus = false



    companion object {
        const val TAG = "TaG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(1000)
            isNickNameSelected=false

            viewModel.initAllData(null,null, false)
            // viewModel.initAllData(schemeId, true)

            addItemViewModel.getPackDataList()
            addItemViewModel.getPackType()
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()

        /*val dateFormat          = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate           = dateFormat.format(Calendar.getInstance().time)
        val threeDaysLaterDate  = dateFormat.format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.time)*/

        /* binding.tvDispatchFromDate.text = todayDate
         binding.tvDispatchToDate.text   = threeDaysLaterDate*/

        remark()

        if (viewModel.pendingOrderID.isNotNullOrBlank()) {
            binding.star1.isEnabled = false
            binding.star3.isEnabled = false
        }

        statusDropDownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusOptions
        )
        binding.autoCompleteStatus.setAdapter(statusDropDownAdapter)

        binding.autoCompleteStatus.setOnItemClickListener { parent, _, position, id ->
            selectedStatus = parent.getItemAtPosition(position).toString()
        }
      binding.ivCallSaleParty.setOnClickListener {
            val phone = binding.etNumber.text.toString()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                it.context.startActivity(intent)
            }
        }
        binding.ivCallPurchaseParty.setOnClickListener {
            val phone = mobileNumberPurchaseParty
            if (!phone.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                it.context.startActivity(intent)
            } else {
                Toast.makeText(it.context, "No mobile number available", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ivCallSubParty.setOnClickListener {
            val phone = mobileNumberSubParty
            if (!phone.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                it.context.startActivity(intent)
            } else {
                Toast.makeText(it.context, "No mobile number available", Toast.LENGTH_SHORT).show()
            }
        }
        binding.autoCompleteStatus.setText("PENDING", false)

        binding.toolbar.apply {
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
            if (viewModel.pendingOrderID.isNotNullOrBlank()) {
                setTitle("Edit Order")

                binding.etSalePartyName.isEnabled = false
                binding.etSubPartyRemark.isEnabled = false

                binding.etPurchasePartyNew.isEnabled = false
                binding.etNicName.isEnabled = false
                binding.radioSubparty.isEnabled = false
                binding.radioSubpartyRemark.isEnabled = false
              //  binding.etSubParty.isEnabled = false
                binding.etDispatchType.isEnabled = false
            //    binding.etTransport.isEnabled = false
            //    binding.etStation.isEnabled = false
                binding.etScheme.isEnabled = false
                binding.radioByNickName.isEnabled = false
                binding.radioBySupplierName.isEnabled = false
                binding.tilPurchaseParty.isEnabled = false
               // binding.etDiscription.isEnabled = false
                binding.etpvtMarka.isEnabled = false
              //  binding.tvDispatchFromDate.isEnabled = false
            //    binding.tvDispatchToDate.isEnabled = false
                binding.tvItemImage.isEnabled = false

            } else {
                nickNameList()
                setTitle("Add Order")
            }

        }
        binding.tvDispatchFromDate.setOnClickListener {
            // openSpinnerBirthdayDialog(true, false)
            // fromDatePicker()
            showFromDatePicker()
        }



        binding.tvDispatchToDate.setOnClickListener {
            // toDatePicker()
            showToDatePicker()
        }
        /*  binding.searchByNickName.setOnClickListener {
              // toDatePicker()
              nickNameSelect()

          }*/


        binding.etSalePartyName.doAfterTextChanged {
            salePartyId = ""
        }
        binding.tilSaletParty.setEndIconOnClickListener {
            //Abhinav
            binding.etSalePartyName.text.clear()
            binding.etAvailableLimit.text?.clear()
            binding.ivCallSaleParty.visibility=View.GONE
            binding.ivCallSubParty.visibility=View.GONE
            binding.etNumber.text?.clear()
            binding.etAverageDays.text?.clear()
            binding.etSubParty.text?.clear()
            binding.etCreditType.text?.clear()
            binding.etNicName.text?.clear()
            binding.etTransport.text?.clear()
            binding.etStation.text?.clear()
            binding.etScheme.text?.clear()
            binding.etPurchaseParty.text?.clear()
            binding.etDiscription.text?.clear()
            binding.etpvtMarka.text?.clear()
            binding.tvDispatchFromDate.text = ""
            binding.tvDispatchToDate.text = ""
        }
        binding.tilSubParty.setEndIconOnClickListener {
            //Abhinav
            binding.ivCallSubParty.visibility=View.GONE
            transportData.clear()
            stationData= emptyList()
            binding.etTransport.text?.clear()
            binding.etSubParty.text?.clear()
            binding.etStation.text?.clear()
        }

        binding.tilScheme.setEndIconOnClickListener{
            isNickNameSelected==false
            purchasePartyData= emptyList()
            isApiCalled=false
            binding.etScheme.text?.clear()
            binding.etNicName.text?.clear()
            binding.etPurchaseParty.text?.clear()
            binding.tilPurchasePartyNew.visibility=View.GONE
            binding.tilPurchaseParty.visibility=View.VISIBLE
            binding.etPurchasePartyNew.text?.clear()
            binding.etPurchaseParty.text?.clear()
            viewModel.getNickNameList(null,null, true)
        //    viewModel.getPurchaseParty(null,null, false)
        }

        binding.tilTransport.setEndIconOnClickListener {
            //Abhinav
            stationData= emptyList()
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
        }

        binding.tilPurchaseParty.setEndIconOnClickListener {
            //Abhinav

            binding.etNicName.text?.clear()
            binding.etPurchaseParty.text?.clear()
            binding.ivCallPurchaseParty.visibility=View.GONE
        }


        binding.etSubParty.doAfterTextChanged {
            subPartyId = ""
        }
        binding.etStation.doAfterTextChanged {
            bookingStationId = ""
        }
        binding.etTransport.doAfterTextChanged {
            transportId = ""
        }
        binding.etDispatchType.doAfterTextChanged {
            dispatchId = ""
        }

        binding.etPurchasePartyNew.doAfterTextChanged {
            purchasePartyId = ""
        }

        binding.etScheme.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                // if (s.isNullOrBlank()) {
                //     viewModel.getInitialPurchaseParty(schemeId, true)
                // }
            }

        })
        binding.etScheme.doAfterTextChanged { data ->

            if (!IS_EDITABLE!!) {
                isSchemeHasData = data?.length!! > 0

                binding.radioByNickName.isChecked = true
                if (!isSchemeHasData) {
                    viewModel.getPurchaseParty(null,null, true)
                }
            }

            // schemeId = ""
            // binding.etPurchaseParty.text.clear()
        }

        binding.etPurchaseParty.doAfterTextChanged {
            purchasePartyId = ""

        }

        binding.etDiscription.doAfterTextChanged {
            when {
                (it?.length ?: 0) > 0 -> {
                    binding.tilDiscription.isErrorEnabled = false
                }

                (it?.length == 0) -> {
                    binding.tilDiscription.isErrorEnabled = true
                }
            }
        }
        binding.etpvtMarka.doAfterTextChanged {
            when {
                (it?.length ?: 0) > 0 -> {
                    binding.tilPVTMarka.isErrorEnabled = false
                }

                (it?.length == 0) -> {
                    binding.tilPVTMarka.isErrorEnabled = true
                }
            }
        }
        binding.etSubPartyRemark.doAfterTextChanged {
            when {
                (it?.length ?: 0) > 0 -> {
                    binding.tilSubPartyRemak.isErrorEnabled = false
                }

                (it?.length == 0) -> {
                    binding.tilSubPartyRemak.isErrorEnabled = true
                }
            }
            // binding.etScheme.doAfterTextChanged {
            //     when {
            //         (it?.length ?: 0) > 0 -> {
            //             schemeId = ""
            //         }
            //     }
            // }
        }
        binding.placeOrder.setOnClickListener {
            if (validate())
                binding.apply {
                //   binding.placeOrder.isEnabled = false

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
                hashMap["SalePartyId"] = salePartyId.toRequestBody()
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
                        hashMap["TransportId"] = transportId.toRequestBody()
                        hashMap["BstationId"] = bookingStationId.toRequestBody()
                    }else{
                        hashMap["TransportId"] = transportId.toRequestBody()
                        hashMap["BstationId"] = bookingStationId.toRequestBody()
                    }
                hashMap["SubPartyasRemark"] = etSubPartyRemark.text.toString().toRequestBody()
                hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()
             //   hashMap["TransportId"] = transportId.toRequestBody()
                hashMap["DispatchTypeID"] = dispatchId.toRequestBody()
            //    hashMap["BstationId"] = bookingStationId.toRequestBody()
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
                hashMap["OrderStatus"] = selectedStatus.toRequestBody()
                hashMap["TraceIdentifier"] = traceIdentifier?.toRequestBody()

                println("PLACING_ORDER 0 $selectedStatus)}")
                if (viewModel.pendingOrderID.isNotNullOrBlank()) hashMap["id"] =
                    (editData?.id ?: "").toRequestBody()


                Log.e("hashmap",hashMap.toString())
               viewModel.placeOrder(hashMap)
                binding.placeOrder.isEnabled=false
            }
        }
        binding.tvAddItem.setOnClickListener {
            if(isNichNameSelect == false){
                if (binding.etPurchaseParty.text.isNullOrBlank()) {
                    binding.etPurchaseParty.requestFocus()
                    binding.tilPurchaseParty.isErrorEnabled = true
                    binding.tilPurchaseParty.setError("You need to select purchase party")
                }  else{
                    openAddBottomSheet()
                }
            }else{
                if (binding.etPurchasePartyNew.text.isNullOrBlank()) {
                    binding.etPurchasePartyNew.requestFocus()
                    binding.tilPurchasePartyNew.isErrorEnabled = true
                    binding.tilPurchasePartyNew.setError("You need to select purchase party")
                }else{
                    openAddBottomSheet()
                }
            }
          /*  if (binding.etPurchaseParty.text.isNullOrBlank()) {
                binding.etPurchaseParty.requestFocus()
                binding.tilPurchaseParty.isErrorEnabled = true
                binding.tilPurchaseParty.setError("You need to select purchase party")
            } else {
                openAddBottomSheet()
            }*/

        }
        binding.tvItemImage.setOnClickListener {
            addImageResultLauncher.launch(
                AddImageActivity.newStartIntent(requireContext(), viewModel.addImageDataList)
            )
        }
        binding.rgStartype.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.star1 -> {
                    binding.tilPVTMarka.visibility=View.GONE
                    pvtMarka = "*"
                    isPvtMarka=false

                }
                R.id.star2 -> pvtMarka = "**"
                R.id.star3 ->{
                    isPvtMarka=true
                    binding.tilPVTMarka.visibility=View.VISIBLE
                    pvtMarka = "***"

                }

            }
        }
        binding.rgSubparty.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_subparty -> {
                    isSubPartyRemark=false
                    binding.tilSubParty.isVisible = true
                    binding.tilSubPartyRemak.isVisible = false
                    binding.llTransPortAndStation.visibility = View.VISIBLE
                    binding.etSubPartyRemark.text.clear()
                    binding.tilSubPartyRemak.isErrorEnabled = false

                    binding.etSubParty.text.clear()

                    binding.etTransport.text?.clear()
                    binding.etStation.text?.clear()
                    transportData.clear()
                    transportDataWithSubparty?.let { transportData!!.addAll(it) }

                    defaultTransportAdapter = DefaultTransportAdapter(
                        requireContext(), R.layout.item_saleparty, transportData!!
                    )
                    binding.etTransport.threshold = 1
                    binding.etTransport.setAdapter(defaultTransportAdapter)

                    //   viewModel.getSalePartyDetailsNew(salePartyId)
              //      viewModel.getSalePartyDetails(salePartyId)

                }

                R.id.radio_subparty_remark -> {
                    isSubPartyRemark=true
                    // if (!salePartyId.isEmpty()) {
                    binding.tilSubParty.isErrorEnabled = false
                    binding.llTransPortAndStation.visibility = View.VISIBLE
                    binding.tilSubPartyRemak.isVisible = true
                    binding.tilSubParty.isVisible = false
                    binding.etSubParty.text.clear()
                    binding.etTransport.text?.clear()
                    binding.etStation.text?.clear()

                    transportData.clear()
                    transportDataWithRemark?.let { transportData!!.addAll(it) }

                    defaultTransportAdapter = DefaultTransportAdapter(
                        requireContext(), R.layout.item_saleparty, transportData!!
                    )
                    binding.etTransport.threshold = 1
                    binding.etTransport.setAdapter(defaultTransportAdapter)
              //      viewModel.getSalePartyDetails(salePartyId)
                    //  viewModel.getSalePartyDetailsNew(salePartyId)
                    // } else viewModel.showMsgAlert("Please Select Sale Party")
                }
            }
        }
        binding.rgPurchaseParty.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
            }
        }
        binding.recyclerViewAddItem.adapter = addItemAdapter
        addItemAdapter.onEditClick =
            { openAddBottomSheet(addItemViewModel.packTypeDataList.indexOf(it)) }
        addItemAdapter.onDeleteClick = { addItemViewModel.deletePackTypeData(it) }

        binding.etSalePartyName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (salePartyData?.any { it -> it.accountName == binding.etSalePartyName.text.toString() } == false) {
                    binding.etSalePartyName.text.clear()
                }
            }
            if (!binding.etSalePartyName.isPopupShowing) {
                binding.etSalePartyName.showDropDown()
            }
        }

        binding.etSalePartyName.setOnClickListener {
            if (!binding.etSalePartyName.isPopupShowing) {
                binding.etSalePartyName.showDropDown()
            }
        }


        binding.etSubParty.setOnFocusChangeListener { v, hasFocus ->
            println("HAS_FOCUS $hasFocus")
            if (!hasFocus) {

                if (subPartyData?.any { it -> it.subPartyName == binding.etSubParty.text.toString() } == false) {
                    binding.etSubParty.text.clear()
                }
            }
            if (!binding.etSubParty.isPopupShowing) {
                binding.etSubParty.showDropDown()
            }
        }
        binding.etSubParty.setOnClickListener {


            binding.etSubParty?.let { subParty ->
                if (!subParty.isPopupShowing) {
                    if (!binding.etSalePartyName.text.isNullOrEmpty()) {
                        subParty.showDropDown()
                    } else {
                        showToast("Please select saleparty first!")
                    }
                }
            }
        }


        binding.etDispatchType.setOnFocusChangeListener { v, hasFocus ->
            println("HAS_FOCUS $hasFocus")
            if (!hasFocus) {

                if (dispatchTypeData?.any { it -> it.value == binding.etDispatchType.text.toString() } == false) {
                    binding.etDispatchType.text.clear()
                }
            }
            if (!binding.etDispatchType.isPopupShowing) {
                binding.etDispatchType.showDropDown()
            }
        }



        binding.etDispatchType.setOnClickListener {
            if (!binding.etDispatchType.isPopupShowing) {
                binding.etDispatchType.showDropDown()
            }
        }




        binding.etTransport.setOnFocusChangeListener { v, hasFocus ->
            if (isSubPartyRemark){
                if (!hasFocus) {
                    if (transportData.isNullOrEmpty() || transportData?.any { it.transportName == binding.etTransport.text.toString() } == false) {
                        binding.etTransport.text.clear()
                    }
                }
                if (!binding.etTransport.isPopupShowing) {
                    binding.etTransport.showDropDown()
                }
            }else{
                if(binding.etSubParty.text.toString().isNotEmpty()){
                    if (!hasFocus) {
                        if (transportData.isNullOrEmpty() || transportData?.any { it.transportName == binding.etTransport.text.toString() } == false) {
                     //       binding.etTransport.text.clear()
                        }
                    }
                    if (!binding.etTransport.isPopupShowing) {
                        binding.etTransport.showDropDown()
                    }
                }else{
                    Toast.makeText(context, "Please first you select subparty", Toast.LENGTH_SHORT).show()
                }
            }



        }
        binding.etTransport.setOnClickListener {
            if (isSubPartyRemark){
                if (!binding.etTransport.isPopupShowing) {
                    binding.etTransport.showDropDown()
                }
            }else{
                if(binding.etSubParty.text.toString().isNotEmpty()){
                    if (!binding.etTransport.isPopupShowing) {
                        binding.etTransport.showDropDown()
                    }
                }else{
                    Toast.makeText(context, "Please first you select subparty", Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.etStation.setOnFocusChangeListener { v, hasFocus ->
            if (isSubPartyRemark){
                if (!hasFocus) {
                    if (stationData?.any { it -> it.stationName == binding.etStation.text.toString() } == false) {
                        binding.etStation.text.clear()
                    }
                }
                if (!binding.etStation.isPopupShowing) {
                    binding.etStation.showDropDown()
                }
            }else{
                if(binding.etTransport.text.toString().isNotEmpty()){
                    if (!hasFocus) {
                        if (stationData?.any { it -> it.stationName == binding.etStation.text.toString() } == false) {
                            binding.etStation.text.clear()
                        }
                    }
                    if (!binding.etStation.isPopupShowing) {
                        binding.etStation.showDropDown()
                    }
                }else{
                    Toast.makeText(context, "Please first you select Transport", Toast.LENGTH_SHORT).show()
                }
            }


        }
        binding.etStation.setOnClickListener {

            if (isSubPartyRemark){
                if (!binding.etStation.isPopupShowing) {
                    binding.etStation.showDropDown()
                }
            }else{
                if(binding.etTransport.text.toString().isNotEmpty()){
                    if (!binding.etStation.isPopupShowing) {
                        binding.etStation.showDropDown()
                    }
                }else{
                    Toast.makeText(context, "Please first you select Transport", Toast.LENGTH_SHORT).show()
                }

            }


        }

     //   private var isApiCalled = false // Flag to track API call

        binding.etPurchaseParty.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                // Call API only if it hasn't been called before
                if (!isApiCalled) {
                    if(binding.etScheme.text.isNotEmpty()){
                        viewModel.getPurchaseParty(null,schemeId, false)
                    }else{
                        viewModel.getPurchaseParty(null,null, false)
                    }

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

        binding.etScheme.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (schemeData?.any { it -> it.schemeName == binding.etScheme.text.toString() } == false) {
                    binding.etScheme.text.clear()
                }
            }
            if (!binding.etScheme.isPopupShowing) {
                binding.etScheme.showDropDown()
            }
        }
        binding.etScheme.setOnClickListener {
            if (!binding.etScheme.isPopupShowing) {
                binding.etScheme.showDropDown()
            }
        }
    }

//not use
    private fun remark(){
      binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbSubparty ->{
                    isSubPartyRemark =false
                    binding.tilSubParty.visibility = View.VISIBLE
                    binding.tilSubPartyRemak.visibility = View.GONE
                }

                R.id.rbRemark ->{
                    isSubPartyRemark =true
                    binding.tilSubParty.visibility = View.GONE
                    binding.tilSubPartyRemak.visibility = View.VISIBLE
                }

            }
        }
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
        AddItemBottomSheetFragment.newInstance(false)
            .show(childFragmentManager, AddItemBottomSheetFragment::class.simpleName)
    }





    private fun parchasePartyListByNickNameNew(nickNameId: String?) {
        isNickNameSelected=true
        viewModel.getPurchasePartyByNickName(nickNameId)


        viewModel.purchasePartyByNickName.observe(viewLifecycleOwner) {
            purchasePartyDataWithNicName = it
            if(purchasePartyDataWithNicName!!.isNotEmpty()){

                if(purchasePartyDataWithNicName!!.size==1){
                    binding.etPurchasePartyNew.clearFocus()

                    binding.etPurchasePartyNew.hideKeyBoard()
                    binding.etPurchasePartyNew.setText(purchasePartyDataWithNicName!!.get(0).accountName)
                    purchasePartyId = purchasePartyDataWithNicName!!.get(0).id.toString()
                    addItemViewModel.getItemsList(purchasePartyId)

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

                    eInvoiceStatus = purPartyItem.eInvoiceStatus == true
                    apiResponseDispatchType()
                    binding.etPurchasePartyNew.setText(purPartyItem.accountName)
                    binding.etPurchasePartyNew.clearFocus()
                    binding.etPurchasePartyNew.hideKeyBoard()
                    binding.tilPurchasePartyNew.isErrorEnabled =
                        !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchasePartyNew.isErrorEnabled)

                    purchasePartyId = purPartyItem.id.toString()
                    addItemViewModel.getItemsList(purchasePartyId)

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
                    findNavController().popBackStack()
                   // findNavController().navigate(R.id.dashboardFragment, null, navOptions)
                }
                it.dismissWithAnimation()
            }

        }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        viewModel.isOrderPlacedSuccess.observe(viewLifecycleOwner) {
            binding.placeOrder.isEnabled=true
            println("MY_SUCCESS_MESSAGE $it")

            if (viewModel.pendingOrderID.isNotNullOrBlank())
                successOrderDialog(
                    "Update Confirmed",
                    it
                )
            else successOrderDialog("Order Placed", it)

        }
        viewModel.isOrderPlaced.observe(viewLifecycleOwner) {

            if (it == true) {
                binding.etSalePartyName.text.clear()

                binding.etSubParty.text.clear()
                binding.etDispatchType.text.clear()
                binding.etAverageDays.text?.clear()
                binding.etAvailableLimit.text?.clear()
                binding.etCreditType.text?.clear()
                binding.etNumber.text?.clear()
                binding.etNicName.text?.clear()
                binding.etPurchaseParty.text?.clear()
                binding.etTransport.text.clear()
                binding.etStation.text.clear()
                binding.rgStartype.check(R.id.star1)
                binding.tilPVTMarka.visibility=View.GONE
                binding.ivCallSaleParty.visibility=View.GONE
                binding.ivCallSubParty.visibility=View.GONE
                binding.ivCallPurchaseParty.visibility=View.GONE
                pvtMarka = "*"
                isPvtMarka=false
                binding.etScheme.text.clear()
                binding.etPurchaseParty.text.clear()
                binding.etDiscription.text?.clear()
                binding.etpvtMarka.text?.clear()

                binding.tvDispatchFromDate.text = ""
                binding.tvDispatchToDate.text = ""
                addItemViewModel.clearWidgetList()
                addItemViewModel.listDataChanged()
                viewModel.addImageDataList.clear()
                salePartyId = ""
                binding.llImageCount.visibility = View.GONE
                binding.etSubPartyRemark.text.clear()
                binding.autoCompleteStatus.setText("PENDING", false)
                addItemViewModel.packTypeDataList = emptyList()
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
                    hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()

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
            viewModel.getEditOrderDataIfNeeded()
        }
        viewModel.editOrderData.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                try {
                    isEditOrder=true
                    editData = it

                    println("GETTING_EDIT_DATA $editData")

                    if (it?.isCancel == true) {
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
                    }

                    val serialNo = it?.voucherCodeNo?.split(" ")?.get(1)
                    val voucherNo = it?.voucherCodeNo?.split(" ")?.get(0)
                    binding.etSerialNo.setText(serialNo)
                    binding.etMarketerCode.setText(voucherNo)

                    var job1: Deferred<Job>? = null


                    if (salePartyData.isNullOrEmpty()) {
                        salePartyData = listOf(
                            SalepartyData(
                                accountID = it?.salePartyId,
                                accountName = it?.salePartyName,
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
                        if (salePartyData?.any { its -> its.accountID == it?.salePartyId } == false) {
                            val tempList: MutableList<SalepartyData> = mutableListOf()
                            tempList.addAll(salePartyData ?: emptyList())
                            tempList.addAll(
                                listOf(
                                    SalepartyData(
                                        accountID = it?.salePartyId,
                                        accountName = it?.salePartyName,
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
                        salePartyData?.filter { saleData -> saleData.accountID == it?.salePartyId }
                    if (!editSaleParty.isNullOrEmpty()) {

                        binding.etSalePartyName.setText(editSaleParty[0].accountName)
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

                    val editSchemeData =
                        schemeData?.filter { statData -> statData.schemeId == it?.schemeId }

                    if (!editSchemeData.isNullOrEmpty()) {
                        IS_EDITABLE = true
                        binding.etScheme.setText(editSchemeData[0].schemeName)
                        schemeId = editSchemeData[0].schemeId ?: ""
                    }


                    /*   if (!it?.schemeId.isNullOrEmpty()) {
                           IS_EDITABLE = true
                           binding.etScheme.setText(editSchemeData[0].schemeName)
                           schemeId = editSchemeData[0].schemeId ?: ""
                       }*/

                    binding.etScheme.setText(it!!.schemeName)
                    binding.etNicName.setText(it!!.nickName)
                    schemeId = (it.schemeId ?: "").toString()

                    binding.tvDispatchToDate.text = it?.deliveryDateTo
                    binding.tvDispatchFromDate.text = it?.deliveryDateFrom

                    binding.etDiscription.setText(it?.remark)
                    binding.rgPurchaseParty.check(R.id.radioBySupplierName)
                    binding.etTransport.setText(it?.transportName)
                    binding.etStation.setText(it?.bstationName)
                    it?.let { data ->
                        data.bstationId?.let { id ->
                            bookingStationId = id
                            stationIDdMainBranch = id
                        }

                        data.transportId?.let { tid ->
                            transportId = tid
                            transportIDdMainBranch = tid
                        }
                    }

                    when (it?.orderCategary ?: "") {
                        "*" -> {
                            binding.tilPVTMarka.visibility=View.GONE
                            binding.rgStartype.check(R.id.star1)
                        }

                        "***" -> {
                            binding.rgStartype.check(R.id.star3)
                            binding.tilPVTMarka.visibility=View.VISIBLE
                            binding.etpvtMarka.setText(it?.pvtMarka)

                        }
                    }


                    var job3: Deferred<Job>? = null


                    println("GETTING_SubParty_DATA ${subPartyData}")
                    val editSubPartyData =
                        subPartyData?.filter { subData -> subData.subPartyId == it?.subPartyId }
                    if (!editSubPartyData.isNullOrEmpty()) {
                        println("GETTING_VALUE_OF_SUB_PARTY 0 ${editSubPartyData[0].subPartyName}")
                        binding.etSubParty.setText(editSubPartyData[0].subPartyName)
                        subPartyId = editSubPartyData[0].subPartyId
                     //   job3 = async { viewModel.getStation(salePartyId, subPartyId) }
                    }

                    println("GETTING_VALUE_OF_SUB_PARTY 0 ${it?.subPartyId }")
                    binding.etSubParty.setText(it?.subPartyName )
                    subPartyId = it?.subPartyId.toString()!!

                    binding.etDispatchType.setText(it?.dispatchType )
                    dispatchId = it?.dispatchTypeID.toString()!!
                    //         job3 = async { viewModel.getStation(it.salePartyId!!, subPartyId) }




                    job3?.join()

                    if (it?.subPartyasRemark.isNotNullOrBlank()) {
                        binding.rgSubparty.check(R.id.radio_subparty_remark)
                        binding.tilSubPartyRemak.visibility = View.VISIBLE
                        binding.llTransPortAndStation.visibility = View.VISIBLE
                        binding.etSubPartyRemark.setText(it?.subPartyasRemark)
                        subPartyId = it.subPartyId.toString()

                        isSubPartyRemark=true
                        isSubPartyRadioSelect = false
                    } else {
                        isSubPartyRemark=false
                        binding.rgSubparty.check(R.id.radio_subparty)
                        binding.tilSubPartyRemak.visibility = View.GONE
                        binding.llTransPortAndStation.visibility = View.VISIBLE
                        isSubPartyRadioSelect = true
                    }


                    // var job4 : Deferred<Job>? = null

                    println("GETTING_TRANSPORT_DATA ${transportData}")
                    val editTransportData =
                        transportData?.filter { transData -> transData.transportId == it?.transportId }
                    if (!editTransportData.isNullOrEmpty()) {
                        binding.etTransport.setText(editTransportData[0].transportName)
                        transportId = editTransportData[0].transportId
                        transportIDdMainBranch = editTransportData[0].transportId
                        // job4 = async { viewModel.getTra }
                    }

                    binding.etTransport.setText(it.transportName)
                    transportId = it.transportId.toString()


                    val editStationData =
                        stationData?.filter { statData -> statData.stationId == it?.bstationId }
                    if (!editStationData.isNullOrEmpty()) {
                        binding.etStation.setText(editStationData[0].stationName)
                        bookingStationId = editStationData[0].stationId
                        stationIDdMainBranch = editStationData[0].stationId
                    }
                    binding.etStation.setText(it.bstationName)
                    bookingStationId = it.bstationId.toString()
                  /*  binding.etStation.setText(editStationData[0].stationName)
                    bookingStationId = editStationData[0].stationId*/



                    val editPurchasePartyData =
                        purchasePartyData?.filter { purchaseData -> purchaseData.id == it?.purchasePartyId }
                    if (!editPurchasePartyData.isNullOrEmpty()) {
                        binding.etPurchaseParty.setText(editPurchasePartyData[0].accountName)
                        purchasePartyId = editPurchasePartyData[0].id ?: ""
                        addItemViewModel.getItemsList(purchasePartyId)
                    }


                    if (it?.purchasePartyId!!.isNotEmpty()) {
                        binding.etPurchaseParty.setText(it!!.purchasePartyName)
                        purchasePartyId = it!!.purchasePartyId ?: ""
                        addItemViewModel.getItemsList(purchasePartyId)
                        it!!.purchasePartyMobileNo?.let { number ->
                            if (number.isNotBlank()) {
                                binding.ivCallPurchaseParty.visibility = View.VISIBLE
                                mobileNumberPurchaseParty = number
                            } else {
                                binding.ivCallPurchaseParty.visibility = View.GONE
                                mobileNumberPurchaseParty = null
                            }
                        } ?: run {
                            binding.ivCallPurchaseParty.visibility = View.GONE
                            mobileNumberPurchaseParty = null
                        }

                    }


                    // for testing
                    val imageUrlList = mutableListOf<String>()

                    it?.docsList?.forEach {
                        if (it?.docsUrls?.contains("pdf", true) == false) {
                            imageUrlList.add(it.docsUrls ?: "")
                        }
                    }

                    // val imageUrlList = arrayOf("https://image.ssspltd.com/sybererp/IMAGES/43029624-ea4a-434c-9a14-d7da24840bad/Screenshot_20241108-211432_Instagram20241111161911939.jpg")
                    val imageUriList = imageUrlList.map { its -> Uri.parse(its) }
                    SharedEditImageUriList.imageUris = imageUriList

                    if (imageUriList.isNotEmpty()) {
                        binding.llImageCount.visibility = View.VISIBLE
                        binding.tvImageCount.text = checkTwoDigitNo(imageUriList.size.toString())
                    }


                    when (it?.orderStatus) {
                        "PENDING" -> {
                            binding.autoCompleteStatus.setText("PENDING", false)
                            selectedStatus = "PENDING"
                        }

                        "HOLD" -> {
                            binding.autoCompleteStatus.setText("HOLD", false)
                            selectedStatus = "HOLD"
                        }
                    }

                    val tempItemDetailList = mutableListOf<PackType>()
                    it?.itemDetailsList?.forEach { itemDetail ->

                        val tempSubItemList = mutableListOf<PackTypeItem>()
                        itemDetail?.itemDetail?.forEach { subItem ->
                            tempSubItemList.add(
                                PackTypeItem(
                                    itemID = subItem?.itemId,
                                    itemName = subItem?.itemName,
                                    itemQuantity = (subItem?.itemQuantity ?: 0).toInt().toString()
                                )
                            )
                        }

                        tempItemDetailList.add(
                            PackType(
                                id = itemDetail?.id ?: "",
                                pcsId = itemDetail?.pcsId ?: "",
                                packName = itemDetail?.packName,
                                qty = itemDetail?.qty?.toInt().toString(),
                                amount = itemDetail?.amount?.toInt().toString(),
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

            binding.etMarketerCode.setText(it?.voucherCode)
            binding.etSerialNo.setText(it?.voucherNO)

            if (it?.isVisible == false) {
                binding.tilRadioStar.visibility = View.GONE

            } else {
                binding.tilRadioStar.visibility = View.VISIBLE
            }
        }

        viewModel.saleParty.observe(viewLifecycleOwner) {
            salePartyData = it
            salePartyAdapter =
                SalePartyAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            binding.etSalePartyName.threshold = 1
            binding.etSalePartyName.setAdapter(salePartyAdapter)
            binding.etSalePartyName.setOnItemClickListener { parent, _, position, _ ->
                val salePartyItem = salePartyAdapter.getItem(position)
                salePartyId = salePartyItem.accountID.toString()
                viewModel.getSalePartyAndStationData(salePartyId, salePartyId, "")
                /* viewModel.getSalePartyDetails(salePartyId)
                 viewModel.getStation(salePartyId, "")*/
                binding.tilSaletParty.isErrorEnabled =
                    !(salePartyItem.accountName.isNotNullOrBlank() || binding.tilSaletParty.isErrorEnabled)

            }

        }


        viewModel.salePartyDetail.observe(viewLifecycleOwner) {
            val apiResponseNew = it ?: return@observe


            if (viewModel.pendingOrderID.isNotNullOrBlank()){
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

                        subPartyItem?.mobileNo?.takeIf { it.isNotBlank() }?.let { phone ->
                            // ✅ Case: mobile number exists & not blank
                            binding.ivCallSubParty.visibility = View.VISIBLE
                            mobileNumberSubParty = phone
                        } ?: run {
                            // ❌ Case: null or blank → reset
                            binding.ivCallSubParty.visibility = View.GONE
                            mobileNumberSubParty = null
                        }

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

                            binding.tilTransport.isErrorEnabled =
                                !(transportId.isNotNullOrBlank() || binding.tilTransport.isErrorEnabled)

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
                            binding.tilTransport.isErrorEnabled =
                                !(transportId.isNotNullOrBlank() || binding.tilTransport.isErrorEnabled)

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
                   //     binding.etTransport.setText(firstTransport.transportName, false)

                   //     transportId = firstTransport.transportId
                   //     transportIDdMainBranch = firstTransport.transportId

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
                      //      binding.etStation.setText(firstStationList[0].stationName,true)
                       //     bookingStationId = firstStationList[0].stationId
                     //       stationIDdMainBranch = firstStationList[0].stationId

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
            else
            {
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

                        subPartyItem?.mobileNo?.takeIf { it.isNotBlank() }?.let { phone ->
                            // ✅ Case: mobile number exists & not blank
                            binding.ivCallSubParty.visibility = View.VISIBLE
                            mobileNumberSubParty = phone
                        } ?: run {
                            // ❌ Case: null or blank → reset
                            binding.ivCallSubParty.visibility = View.GONE
                            mobileNumberSubParty = null
                        }

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

                    apiResponseNew?.mobileNo?.takeIf { it.isNotBlank() }?.let { phone ->
                        // ✅ Case: mobile number exists & not blank
                        binding.ivCallSaleParty.visibility = View.VISIBLE
                        mobileNumberSubParty = phone
                    } ?: run {
                        // ❌ Case: null or blank → reset
                        binding.ivCallSaleParty.visibility = View.GONE
                        mobileNumberSubParty = null
                    }


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
                        binding.tilTransport.isErrorEnabled =
                            !(transportId.isNotNullOrBlank() || binding.tilTransport.isErrorEnabled)


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

            // Indian locale for lakh/crore grouping
            val formatter = NumberFormat.getInstance(Locale("en", "IN"))

            val formattedLimit = avlLimit.toLongOrNull()?.let { formatter.format(it) } ?: avlLimit

            binding.etAvailableLimit.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (avlLimit.contains("-") || avlLimit == "0") R.color.error_text else R.color.green
                )
            )

            binding.etAvailableLimit.setText(formattedLimit)

            apiResponseNew.mobileNo?.let { phone ->
                if (phone.isNotBlank()) {
                    binding.etNumber.setText(apiResponseNew.mobileNo)
                    binding.ivCall.visibility = View.GONE
                    binding.tillNumber.visibility = View.GONE
                    binding.etNumber.visibility = View.GONE
                    binding.ivCallSaleParty.visibility = View.VISIBLE
                } else {
                    binding.ivCall.visibility = View.GONE
                }
            }




            binding.etAverageDays.setText(apiResponseNew.avgDays.toString())
            binding.etCreditType.setText(apiResponseNew.partyType.toString())
        }

        viewModel.station.observe(viewLifecycleOwner) {
            /*    stationAdapter = StationAdapter(
                    requireContext(), R.layout.item_saleparty, it.orEmpty()
                )
                stationData = it
                binding.etStation.threshold = 1
                binding.etStation.setAdapter(stationAdapter)
                if (it.isNullOrEmpty()) {
                    stationAdapter.setdefStationList(emptyList()) // Clear and update
                } else {
                    stationAdapter.setdefStationList(it)
                }
                binding.etStation.setOnItemClickListener { parent, _, position, _ ->
                    val stationId = stationAdapter.getItem(position)
                    bookingStationId = stationId?.stationId.toString()
                    binding.tilStation.isErrorEnabled =
                        !(stationId?.stationId.isNotNullOrBlank() || binding.tilStation.isErrorEnabled)

                }*/
        }


        viewModel.purchaseParty_.observe(viewLifecycleOwner) {
          //  purchasePartyData = it?.filterNonEmptyFields()
            val finalList = it?.filterNonEmptyFields()?.toMutableList() ?: mutableListOf()
            if (finalList.isNotEmpty()) {
                repeat(8) { index ->
                    finalList.add(
                        PurchasePartyData(
                            id = "FAKE_${index + 1}",        // special id for fake items
                            nickName = "",
                            mobileNo = "",
                            nickNameId = "FAKE_ID_${index + 1}",
                            accountName = "",
                            nickNameStatus = null,
                            fontColor = "#999999",           // gray text (optional)
                            eInvoiceStatus = false
                        )
                    )
                }
            }

            purchasePartyData = finalList
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
                eInvoiceStatus = purPartyItem.eInvoiceStatus == true
                apiResponseDispatchType()
                binding.etPurchaseParty.setText(purPartyItem.accountName.toString())

                // Show/hide call icon based on mobile number
                purPartyItem.mobileNo?.let { number ->
                    if (number.isNotBlank()) {
                        binding.ivCallPurchaseParty.visibility = View.VISIBLE
                        mobileNumberPurchaseParty = number
                    } else {
                        binding.ivCallPurchaseParty.visibility = View.GONE
                        mobileNumberPurchaseParty = null
                    }
                } ?: run {
                    binding.ivCallPurchaseParty.visibility = View.GONE
                    mobileNumberPurchaseParty = null
                }

                binding.etPurchaseParty.clearFocus()
                binding.etPurchaseParty.hideKeyBoard()
                binding.tilPurchaseParty.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchaseParty.isErrorEnabled)
                purchasePartyId = purPartyItem.id.toString()
                addItemViewModel.getItemsList(purchasePartyId)

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

        viewModel.scheme.observe(viewLifecycleOwner) {
            val activeList = it.orEmpty().filter { it.activeStatus }
            schemeAdapter = SchemeAdapter(requireContext(), R.layout.item_saleparty, activeList.orEmpty())
            schemeData = activeList
            binding.etScheme.threshold = 1
            binding.etScheme.setAdapter(schemeAdapter)
            binding.etScheme.setOnItemClickListener { parent, _, position, _ ->
                val schemeItem = schemeAdapter.getItem(position)
                schemeId = schemeItem.schemeId.toString()
                isApiCalled=false
                binding.etPurchaseParty.text.clear()
             //   viewModel.getPurchaseParty(null,schemeItem.schemeId, true)
                binding.etNicName.text.clear()
                viewModel.getNickNameList(null,schemeItem.schemeId, true)
           //     viewModel.getPurchaseParty(null,schemeId, false)

                binding.tilScheme.isErrorEnabled =
                    !(schemeItem.schemeId.isNotNullOrBlank() || binding.tilScheme.isErrorEnabled)
            }


        }

        addItemViewModel.isListAvailable.observe(viewLifecycleOwner) {
            addItemAdapter.submitList(addItemViewModel.widgetList)
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


    private fun checkCleanInValidSelectedData() {
        if (salePartyData?.any { it.accountName == binding.etSalePartyName.text.toString() } == false) {
            binding.etSalePartyName.text.clear()
        }

        if (isNickNameSelected) {
            if (purchasePartyData?.any {
                    it.nickName.equals(
                        binding.etPurchaseParty.text.trim().toString()
                    )
                } == false) {
                binding.etPurchaseParty.text.clear()
            }
        } else {
            if (purchasePartyData?.any {
                    it.accountName.equals(
                        binding.etPurchaseParty.text.trim().toString()
                    )
                } == false) {
                binding.etPurchaseParty.text.clear()
            }
        }

        if (stationData?.any { it.stationName == binding.etStation.text.toString() } == false) {
            binding.etStation.text.clear()
        }

        if (transportData.isNullOrEmpty() || transportData?.any { it.transportName == binding.etTransport.text.toString() } == false) {
            binding.etTransport.text.clear()
        }

        if (subPartyData?.any { it.subPartyName == binding.etSubParty.text.toString() } == false) {
            binding.etSubParty.text.clear()
        }

        if (salePartyData?.any { it.accountName == binding.etSalePartyName.text.toString() } == false) {
            binding.etSalePartyName.text.clear()
        }
    }

    private fun validate(): Boolean = with(binding) {
        if(viewModel.pendingOrderID.isNotNullOrBlank()){
            //    viewModel.addItemDataList.clear()
        }else{
            checkCleanInValidSelectedData()
        }

        if (etSalePartyName.text.isNullOrBlank()) {
            etSalePartyName.requestFocus()
            tilSaletParty.isErrorEnabled = true
            tilSaletParty.setError("You need to select sale party")
            return false
        } else if (etAvailableLimit.text.isNullOrBlank()) {
            etAvailableLimit.requestFocus()
            tilAvailableLimit.isErrorEnabled = true
            tilAvailableLimit.setError("Out of limit, please contact to sss")
            return false
        } else if (etAverageDays.text.isNullOrBlank()) {
            etAverageDays.requestFocus()
            tilAverageDays.isErrorEnabled = true
            tilAverageDays.setError("You need to select sale party")
            return false
        } else if (radioSubparty.isChecked && etSubParty.text.isBlank() && (!etSubParty.text.toString()
                .equals("self", true))
        ) {
            etSubParty.requestFocus()
            tilSubParty.isErrorEnabled = true
            tilSubParty.setError("You need to select sub party")
            return false
        } else if (radioSubpartyRemark.isChecked && etSubPartyRemark.text.isBlank()) {
            etSubPartyRemark.requestFocus()
            tilSubPartyRemak.isErrorEnabled = true
            tilSubPartyRemak.setError("Please enter remarks")
            return false
        } else if (etTransport.text.isBlank()) {
            etTransport.requestFocus()
            tilTransport.isErrorEnabled = true
            tilTransport.setError("You need to select transport")
            return false
        } else if (etStation.text.isBlank()) {
            etStation.requestFocus()
            tilStation.isErrorEnabled = true
            tilStation.setError("You need to select station")
            return false
        }/* else if (etDispatchType.text.isBlank()) {
            etDispatchType.requestFocus()
            tilStation.isErrorEnabled = true
            tilStation.setError("You need to select station")
            return false
        } */

        else if(isNichNameSelect == true&&etPurchasePartyNew.text.isBlank()){
                etPurchasePartyNew.requestFocus()
                tilPurchasePartyNew.isErrorEnabled = true
                tilPurchasePartyNew.setError("You need to select purchase party")
                return false

        }else if(isNickNameSelected==false&& etPurchaseParty.text.isBlank()){
                etPurchaseParty.requestFocus()
                tilPurchaseParty.isErrorEnabled = true
                tilPurchaseParty.setError("You need to select purchase party")
                return false


        }

        else if (isPvtMarka && etpvtMarka.text?.isBlank() == true) {  // ✅ Safe null check
            etpvtMarka.requestFocus()
            tilPVTMarka.isErrorEnabled = true
            tilPVTMarka.setError("Please enter PVT Marka")
            return false
        }
        else if (etDispatchType.text.isBlank()) {
            etDispatchType.requestFocus()
            tilDispatchType.isErrorEnabled = true
            tilDispatchType.setError("You need to select Dispatch Type")
            return false
        }

        else if (tvDispatchFromDate.text.isBlank()) {
            tvDispatchFromDate.requestFocus()
            tvDispatchFromDate.setBackgroundResource(R.drawable.red_outline)
            tvDispatchFromDate.error = "Please enter From date"
            return false
        } else if (tvDispatchToDate.text.isBlank()) {
            tvDispatchToDate.requestFocus()
            tvDispatchToDate.setBackgroundResource(R.drawable.red_outline)
            tvDispatchToDate.error = "Please enter To date"
            return false
        } else if (viewModel.addItemDataList.size == 0) {
            tvAddItem.requestFocus()
            tvAddItem.setBackgroundResource(R.drawable.red_outline)
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

    private fun showFromDatePicker() {
        val fromDate = Calendar.getInstance()

        // Check if there's already a selected "From Date"
        if (!binding.tvDispatchFromDate.text.isNullOrEmpty()) {
            fromDate.time = dateFormat.parse(binding.tvDispatchFromDate.text.toString())!!
        }

        val fromDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                fromDate.set(Calendar.YEAR, year)
                fromDate.set(Calendar.MONTH, monthOfYear)
                fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.tvDispatchFromDate.text = dateFormat.format(fromDate.time)
                binding.tvDispatchFromDate.setBackgroundResource(R.drawable.layout_outline)
                binding.tvDispatchFromDate.error = null
                // Check if ToDate needs to be updated based on the selected FromDate
                if (!binding.tvDispatchToDate.text.isNullOrEmpty()) {
                    val currentToDate = Calendar.getInstance().apply {
                        time = dateFormat.parse(binding.tvDispatchToDate.text.toString())!!
                    }
                    if (currentToDate.before(fromDate)) {
                        val updatedToDate = fromDate.clone() as Calendar
                        updatedToDate.add(Calendar.DAY_OF_YEAR, 0)
                        binding.tvDispatchToDate.text = dateFormat.format(updatedToDate.time)
                    }
                }


            }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            fromDateListener,
            fromDate.get(Calendar.YEAR),
            fromDate.get(Calendar.MONTH),
            fromDate.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict to today's date or future dates, up to 3 months from today
        val today = Calendar.getInstance()
        datePickerDialog.datePicker.minDate = today.timeInMillis

        val maxDate = Calendar.getInstance().apply { add(Calendar.MONTH, 3) }.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }


    private fun showToDatePicker() {
        val toDate = Calendar.getInstance()

        if (!binding.tvDispatchToDate.text.isNullOrEmpty()) {
            toDate.time = dateFormat.parse(binding.tvDispatchToDate.text.toString())!!
        }

        val toDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                toDate.set(Calendar.YEAR, year)
                toDate.set(Calendar.MONTH, monthOfYear)
                toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.tvDispatchToDate.text = dateFormat.format(toDate.time)
                binding.tvDispatchToDate.setBackgroundResource(R.drawable.layout_outline)
                binding.tvDispatchToDate.error = null
            }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            toDateListener,
            toDate.get(Calendar.YEAR),
            toDate.get(Calendar.MONTH),
            toDate.get(Calendar.DAY_OF_MONTH)
        )

        if (!binding.tvDispatchFromDate.text.isNullOrEmpty()) {
            val fromDate = Calendar.getInstance().apply {
                time = dateFormat.parse(binding.tvDispatchFromDate.text.toString())!!
            }
            datePickerDialog.datePicker.minDate = fromDate.timeInMillis
            // + (1 * 24 * 60 * 60 * 1000)  // +3 days from fromDate

        }

        // Restrict the ToDate to be at least 3 days after fromDate, and at most 3 months from today's date
        val maxDate = Calendar.getInstance().apply { add(Calendar.MONTH, 3) }.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate


        datePickerDialog.show()
    }


    private fun apiResponseDispatchType(){
        viewModel.getDispatchTypeList.observe(viewLifecycleOwner) {

            val originalList = it.orEmpty()

            val filteredList = if (!eInvoiceStatus) {
                originalList.filter { item -> item.value != "BILL TO SHIP TO" }
            } else {
                originalList
            }
            dispatchAdapter = DispatchAdapter(requireContext(), R.layout.item_saleparty, filteredList)


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
            // if not empty, add 3 fake items
            val finalList = nicNaneData?.toMutableList() ?: mutableListOf()
            if (finalList.isNotEmpty()) {
                repeat(8) { index ->
                    finalList.add(
                        PurchasePartyData(
                            id = "FAKE_${index + 1}",         // special id for fake items
                            nickName = "",
                            mobileNo  = "",
                            nickNameId = "FAKE_ID_${index+1}",
                            accountName = "",
                            nickNameStatus = null,
                            fontColor = "#999999",            // gray text (optional)
                            eInvoiceStatus = false
                        )
                    )
                }
            }
            //nick name list
            if (!::nickNameAdapter.isInitialized) {

                nickNameAdapter = PurchasePartyAdapter(
                    true,
                    requireContext(),
                    R.layout.item_saleparty,
                    finalList.orEmpty()
                )

                binding.etNicName.threshold = 1
                binding.etNicName.setAdapter(nickNameAdapter)

            }
            else {
                nickNameAdapter.updateType(false)
                nickNameAdapter.updateData(finalList.orEmpty())
            }


            binding.etNicName.setOnItemClickListener { parent, v, position, l ->

                isNichNameSelect=true

                // val purPartyItem = parent.getItemAtPosition(position)  as PurchasePartyData
                val purPartyItem = parent.getItemAtPosition(position) as PurchasePartyData

                Log.i("TaG", "selected purchase party --------->${purPartyItem}")
                eInvoiceStatus = purPartyItem.eInvoiceStatus == true
                apiResponseDispatchType()
                binding.tilPurchasePartyNew.visibility=View.VISIBLE
                binding.tilPurchaseParty.visibility=View.GONE
                binding.etPurchasePartyNew.text?.clear()
                purchasePartyId = purPartyItem.nickNameId.toString()

                //  parchasePartyListByNickName("8873CD90-A606-403D-B2A2-372580DA710B")
                addItemViewModel.getItemsList(purPartyItem.nickNameId.toString())
                parchasePartyListByNickNameNew(purchasePartyId)

                binding.etNicName.setText(purPartyItem.nickName.toString())
                binding.etNicName.clearFocus()
                binding.etNicName.hideKeyBoard()
                binding.tilNickNameList.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilNickNameList.isErrorEnabled)

            }

        }
    }









}


