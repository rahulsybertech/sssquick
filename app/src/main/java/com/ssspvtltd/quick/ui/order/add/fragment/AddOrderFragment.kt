package com.ssspvtltd.quick.ui.order.add.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
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
import com.ssspvtltd.quick.model.order.add.PurchasePartyData
import com.ssspvtltd.quick.model.order.add.SalepartyData
import com.ssspvtltd.quick.model.order.add.SchemeData
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.model.order.add.additem.PackType
import com.ssspvtltd.quick.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick.model.order.add.editorder.EditOrderDataNew
import com.ssspvtltd.quick.model.order.add.salepartydetails.AllStation
import com.ssspvtltd.quick.model.order.add.salepartydetails.DefTransport
import com.ssspvtltd.quick.model.order.add.salepartydetails.SubParty
import com.ssspvtltd.quick.ui.order.add.activity.AddImageActivity
import com.ssspvtltd.quick.ui.order.add.adapter.DefaultTransportAdapter
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class AddOrderFragment : BaseFragment<FragmentAddOrderBinding, AddOrderViewModel>() {
    // private val addItemViewModel by activityViewModels<AddItemViewModel>()
    override val inflate: InflateF<FragmentAddOrderBinding> get() = FragmentAddOrderBinding::inflate
    override fun initViewModel(): AddOrderViewModel = getViewModel()

    private val addItemViewModel by viewModels<AddItemViewModel>()

    private var salePartyId: String = ""

    // changes
    private var subPartyId: String = ""
    private var purchasePartyId: String = ""
    private var pvtMarka: String = "*"
    private var bookingStationId: String = ""
    private var transportId: String = ""
    private var schemeId: String = ""
    private var selectedStatus: String = "PENDING"

    private var purchasePartyData: List<PurchasePartyData>? = emptyList()
    private var salePartyData: List<SalepartyData>? = emptyList()
    private var subPartyData: List<SubParty>? = emptyList()
    private var stationData: List<AllStation>? = emptyList()
    private var schemeData: List<SchemeData>? = emptyList()
    private var transportData: List<DefTransport>? = emptyList()

    private lateinit var subPartyAdapter: SubPartyAdapter
    private lateinit var defaultTransportAdapter: DefaultTransportAdapter
    private lateinit var stationAdapter: StationAdapter
    private lateinit var purchasePartyAdapter: PurchasePartyAdapter
    private lateinit var schemeAdapter: SchemeAdapter
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

    companion object {
        const val TAG = "TaG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(1000)
            viewModel.initAllData(schemeId, true)
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
        binding.autoCompleteStatus.setText("PENDING", false)

        binding.toolbar.apply {
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
            if (viewModel.pendingOrderID.isNotNullOrBlank()) {
                setTitle("Edit Order")

                binding.etSalePartyName.isEnabled = false
                binding.radioSubparty.isEnabled = false
                binding.radioSubpartyRemark.isEnabled = false
                binding.etSubParty.isEnabled = false
                binding.etTransport.isEnabled = false
                binding.etStation.isEnabled = false
                binding.etScheme.isEnabled = false
                binding.radioByNickName.isEnabled = false
                binding.radioBySupplierName.isEnabled = false
                binding.tilPurchaseParty.isEnabled = false
                binding.etDiscription.isEnabled = false
                binding.tvDispatchFromDate.isEnabled = false
                binding.tvDispatchToDate.isEnabled = false
                binding.tvItemImage.isEnabled = false

            } else setTitle("Add Order")
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

        binding.etSalePartyName.doAfterTextChanged {
            salePartyId = ""
        }
        binding.tilSaletParty.setEndIconOnClickListener {
            //Abhinav
            binding.etSalePartyName.text.clear()
            binding.etAvailableLimit.text?.clear()
            binding.etAverageDays.text?.clear()
            binding.etSubParty.text?.clear()
            binding.etTransport.text?.clear()
            binding.etStation.text?.clear()
            binding.etScheme.text?.clear()
            binding.etPurchaseParty.text?.clear()
            binding.etDiscription.text?.clear()
            binding.tvDispatchFromDate.text = ""
            binding.tvDispatchToDate.text = ""
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
            isSchemeHasData = data?.length!! > 0

            binding.radioByNickName.isChecked = true
            if (!isSchemeHasData) {
                viewModel.getPurchaseParty(null, true)
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
            if (validate()) binding.apply {
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
                hashMap["SubPartyId"] = subPartyId.toRequestBody()
                hashMap["SubPartyasRemark"] = etSubPartyRemark.text.toString().toRequestBody()
                hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()
                hashMap["TransportId"] = transportId.toRequestBody()
                hashMap["BstationId"] = bookingStationId.toRequestBody()
                hashMap["SchemeId"] = schemeId.toRequestBody()
                hashMap["OrderCategary"] = pvtMarka.toRequestBody()
                hashMap["DeliveryDateFrom"] = tvDispatchFromDate.text.toString().toRequestBody()
                hashMap["DeliveryDateTo"] = tvDispatchToDate.text.toString().toRequestBody()
                hashMap["Remark"] = etDiscription.text.toString().toRequestBody()
                hashMap["TotalQty"] = "0".toRequestBody()
                hashMap["TotalAmt"] = "0".toRequestBody()
                hashMap["OrderTypeName"] = "TRADING".toRequestBody()
                hashMap["OrderStatus"] = selectedStatus.toRequestBody()
                hashMap["TraceIdentifier"] = traceIdentifier?.toRequestBody()
                println("PLACING_ORDER 0 ${Gson().toJson(hashMap)}")
                if (viewModel.pendingOrderID.isNotNullOrBlank()) hashMap["id"] =
                    (editData?.id ?: "").toRequestBody()

                viewModel.placeOrder(hashMap)
            }
        }
        binding.tvAddItem.setOnClickListener {
            if (binding.etPurchaseParty.text.isNullOrBlank()) {
                binding.etPurchaseParty.requestFocus()
                binding.tilPurchaseParty.isErrorEnabled = true
                binding.tilPurchaseParty.setError("You need to select purchase party")
            } else {
                openAddBottomSheet()
            }

        }
        binding.tvItemImage.setOnClickListener {
            addImageResultLauncher.launch(
                AddImageActivity.newStartIntent(requireContext(), viewModel.addImageDataList)
            )
        }
        binding.rgStartype.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.star1 -> pvtMarka = "*"
                R.id.star2 -> pvtMarka = "**"
                R.id.star3 -> pvtMarka = "***"
            }
        }
        binding.rgSubparty.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_subparty -> {
                    binding.tilSubParty.isVisible = true
                    binding.tilSubPartyRemak.isVisible = false
                    binding.etSubPartyRemark.text.clear()
                    binding.tilSubPartyRemak.isErrorEnabled = false
                    viewModel.getSalePartyDetails(salePartyId)

                }

                R.id.radio_subparty_remark -> {
                    // if (!salePartyId.isEmpty()) {
                    binding.tilSubParty.isErrorEnabled = false
                    binding.tilSubPartyRemak.isVisible = true
                    binding.tilSubParty.isVisible = false
                    viewModel.getSalePartyDetails(salePartyId)
                    // } else viewModel.showMsgAlert("Please Select Sale Party")
                }
            }
        }
        binding.rgPurchaseParty.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioByNickName -> {
                    binding.etPurchaseParty.text.clear()
                    if (isSchemeHasData) {
                        viewModel.getInitialPurchaseParty(schemeId, true)
                    } else {
                        viewModel.getPurchaseParty(null, true)
                    }
                    if (::purchasePartyAdapter.isInitialized) purchasePartyAdapter.updateType(true)
                    isNickNameSelected = true
                }

                R.id.radioBySupplierName -> {
                    binding.etPurchaseParty.text.clear()
                    if (isSchemeHasData) {
                        viewModel.getInitialPurchaseParty(schemeId, false)
                    } else {
                        viewModel.getPurchaseParty(null, false)
                    }
                    if (::purchasePartyAdapter.isInitialized) purchasePartyAdapter.updateType(false)
                    isNickNameSelected = false
                }
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
            if (!binding.etSubParty.isPopupShowing) {
                binding.etSubParty.showDropDown()
            }
        }

        binding.etTransport.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (transportData.isNullOrEmpty() || transportData?.any { it.transportName == binding.etTransport.text.toString() } == false) {
                    binding.etTransport.text.clear()
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

        binding.etStation.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (stationData?.any { it -> it.stationName == binding.etStation.text.toString() } == false) {
                    binding.etStation.text.clear()
                }
            }
            if (!binding.etStation.isPopupShowing) {
                binding.etStation.showDropDown()
            }
        }
        binding.etStation.setOnClickListener {
            if (!binding.etStation.isPopupShowing) {
                binding.etStation.showDropDown()
            }
        }

        binding.etPurchaseParty.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {

                if (isNickNameSelected) {
                    if (purchasePartyData?.any { it ->
                            it.nickName.equals(
                                binding.etPurchaseParty.text.trim().toString()
                            )
                        } == false) {
                        binding.etPurchaseParty.text.clear()
                    }
                } else {
                    if (purchasePartyData?.any { it ->
                            it.accountName.equals(
                                binding.etPurchaseParty.text.trim().toString()
                            )
                        } == false) {
                        binding.etPurchaseParty.text.clear()
                    }
                }

            }

            if (!binding.etPurchaseParty.isPopupShowing) {
                binding.etPurchaseParty.showDropDown()
            }
        }


        binding.etPurchaseParty.setOnClickListener {
            if (!binding.etPurchaseParty.isPopupShowing) {
                binding.etPurchaseParty.showDropDown()
            }
        }

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
        AddItemBottomSheetFragment.newInstance()
            .show(childFragmentManager, AddItemBottomSheetFragment::class.simpleName)
    }

    private fun successOrderDialog(title: String, msg: String) {
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE).apply {
            titleText = title
            contentText = msg
            setCancelable(false)
            confirmText = getString(R.string.ok)
            confirmButtonBackgroundColor = getColor(context, R.color.red_2)
            setConfirmClickListener {
                it.dismissWithAnimation()
                if (viewModel.pendingOrderID.isNotNullOrBlank()) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.addOrderFragment, true)
                        .build()
                    findNavController().navigate(R.id.dashboardFragment, null, navOptions)
                }
            }

        }.show()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        viewModel.isOrderPlacedSuccess.observe(viewLifecycleOwner) {
            println("MY_SUCCESS_MESSAGE $it")

            if (viewModel.pendingOrderID.isNotNullOrBlank()) successOrderDialog(
                "Update Confirmed",
                it
            )
            else successOrderDialog("Order Placed", it)
        }
        viewModel.isOrderPlaced.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.etSalePartyName.text.clear()
                binding.etSubParty.text.clear()
                binding.etAverageDays.text?.clear()
                binding.etAvailableLimit.text?.clear()
                binding.etTransport.text.clear()
                binding.etStation.text.clear()
                binding.etScheme.text.clear()
                binding.etPurchaseParty.text.clear()
                binding.etDiscription.text?.clear()
                binding.tvDispatchFromDate.text = ""
                binding.tvDispatchToDate.text = ""
                addItemViewModel.clearWidgetList()
                addItemViewModel.listDataChanged()
                viewModel.addImageDataList.clear()
                salePartyId = ""
                binding.llImageCount.visibility = View.GONE
                binding.etSubPartyRemark.text.clear()

                subPartyData = emptyList()
                stationData = emptyList()
                transportData = emptyList()
                /*purchasePartyData       = emptyList()
                schemeData              = emptyList()*/

                subPartyAdapter.setSubPartyData(subPartyData!!)
                stationAdapter.setdefStationList(stationData!!)
                defaultTransportAdapter.setDefTransportList(transportData!!)
                /*purchasePartyAdapter.setPurchasePartyList(purchasePartyData!!)
                //check
                schemeAdapter.setSchemeData(schemeData!!)*/

                viewModel.getVoucher()
            }
        }
        viewModel.isOrderPlacedLimitError.observe(viewLifecycleOwner) { errorMsg ->
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
                    hashMap["SubPartyId"] = subPartyId.toRequestBody()
                    hashMap["SubPartyasRemark"] = etSubPartyRemark.text.toString().toRequestBody()
                    hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()
                    hashMap["TransportId"] = transportId.toRequestBody()
                    hashMap["BstationId"] = bookingStationId.toRequestBody()
                    hashMap["SchemeId"] = schemeId.toRequestBody()
                    hashMap["OrderCategary"] = pvtMarka.toRequestBody()
                    hashMap["DeliveryDateFrom"] = tvDispatchFromDate.text.toString().toRequestBody()
                    hashMap["DeliveryDateTo"] = tvDispatchToDate.text.toString().toRequestBody()
                    hashMap["Remark"] = etDiscription.text.toString().toRequestBody()
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
                        binding.etScheme.setText(editSchemeData[0].schemeName)
                        schemeId = editSchemeData[0].schemeId ?: ""
                    }



                    binding.tvDispatchToDate.text = it?.deliveryDateTo
                    binding.tvDispatchFromDate.text = it?.deliveryDateFrom
                    binding.etDiscription.setText(it?.remark)
                    binding.rgPurchaseParty.check(R.id.radioBySupplierName)
                    binding.etTransport.setText(it?.transportName)
                    binding.etStation.setText(it?.bstationName)

                    when (it?.pvtMarka ?: "") {
                        "*" -> {
                            binding.rgStartype.check(R.id.star1)
                        }

                        "***" -> {
                            binding.rgStartype.check(R.id.star3)
                        }
                    }

                    if (it?.subPartyasRemark.isNotNullOrBlank()) {
                        binding.rgSubparty.check(R.id.radio_subparty_remark)
                        binding.tilSubPartyRemak.visibility = View.VISIBLE
                        binding.etSubPartyRemark.setText(it?.subPartyasRemark)
                        isSubPartyRadioSelect = false
                    } else {
                        binding.rgSubparty.check(R.id.radio_subparty)
                        binding.tilSubPartyRemak.visibility = View.GONE
                        isSubPartyRadioSelect = true
                    }

                    var job3: Deferred<Job>? = null


                    println("GETTING_SubParty_DATA ${subPartyData}")
                    val editSubPartyData =
                        subPartyData?.filter { subData -> subData.subPartyId == it?.subPartyId }
                    if (!editSubPartyData.isNullOrEmpty()) {
                        println("GETTING_VALUE_OF_SUB_PARTY 0 ${editSubPartyData[0].subPartyName}")
                        binding.etSubParty.setText(editSubPartyData[0].subPartyName)
                        subPartyId = editSubPartyData[0].subPartyId
                        job3 = async { viewModel.getStation(salePartyId, subPartyId) }
                    }

                    job3?.join()


                    // var job4 : Deferred<Job>? = null

                    println("GETTING_TRANSPORT_DATA ${transportData}")
                    val editTransportData =
                        transportData?.filter { transData -> transData.transportId == it?.transportId }
                    if (!editTransportData.isNullOrEmpty()) {
                        binding.etTransport.setText(editTransportData[0].transportName)
                        transportId = editTransportData[0].transportId
                        // job4 = async { viewModel.getTra }
                    }


                    val editStationData =
                        stationData?.filter { statData -> statData.stationId == it?.bstationId }
                    if (!editStationData.isNullOrEmpty()) {
                        binding.etStation.setText(editStationData[0].stationName)
                        bookingStationId = editStationData[0].stationId
                    }

                    val editPurchasePartyData =
                        purchasePartyData?.filter { purchaseData -> purchaseData.id == it?.purchasePartyId }
                    if (!editPurchasePartyData.isNullOrEmpty()) {
                        binding.etPurchaseParty.setText(editPurchasePartyData[0].accountName)
                        purchasePartyId = editPurchasePartyData[0].id ?: ""
                        addItemViewModel.getItemsList(purchasePartyId)
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
            subPartyAdapter = SubPartyAdapter(
                requireContext(), R.layout.item_saleparty, it?.subPartyList ?: emptyList()
            )
            subPartyData = it?.subPartyList
            binding.etSubParty.threshold = 1
            binding.etSubParty.setAdapter(subPartyAdapter)
            binding.etSubParty.setOnItemClickListener { parent, _, position, _ ->
                val subPartyItem = subPartyAdapter.getItem(position)
                subPartyId = subPartyItem.subPartyId
                binding.tilSubParty.isErrorEnabled =
                    !(subPartyItem.subPartyId.isNotNullOrBlank() || binding.tilSubParty.isErrorEnabled)
                viewModel.getStation(salePartyId, subPartyId)
            }

            defaultTransportAdapter = DefaultTransportAdapter(
                requireContext(), R.layout.item_saleparty, it?.defTransport.orEmpty()
            )
            transportData = it?.defTransport

            binding.etTransport.threshold = 1
            binding.etTransport.setAdapter(defaultTransportAdapter)
            binding.etTransport.setOnItemClickListener { parent, _, position, _ ->
                val tId = defaultTransportAdapter.getItem(position)
                transportId = tId?.transportId.toString()
                binding.tilTransport.isErrorEnabled =
                    !(tId?.transportId.isNotNullOrBlank() || binding.tilTransport.isErrorEnabled)
            }
            var avlLimit = "0"
            if (it?.avlLimit != null && (it.avlLimit ?: "0").toDouble() != 0.toDouble()) {
                avlLimit = it.avlLimit.toString()
            } else {
                binding.autoCompleteStatus.setText("PENDING", false)
            }

            if (avlLimit.contains("-") || avlLimit == "0") {
                binding.etAvailableLimit.setTextColor(
                    getColor(
                        requireContext(),
                        R.color.error_text
                    )
                )
            } else {
                binding.etAvailableLimit.setTextColor(getColor(requireContext(), R.color.green))
            }

            binding.etAvailableLimit.setText("${avlLimit}")
            binding.etAverageDays.setText(it?.avgDays.toString())
            binding.etAverageDays.setText(it?.avgDays.toString())
            binding.etSubParty.setText(it?.defSubPartyName.orEmpty(), false)
            // binding.etStation.setText(it?.defTransport?.get(0)?.defStation?.get(0)?.stationName.orEmpty())
            bookingStationId = it?.defTransport?.get(0)?.defStation?.get(0)?.stationId.toString()
            // binding.etTransport.setText(it?.defTransport?.get(0)?.transportName)
            transportId = (it?.defTransport?.get(0)?.transportId.toString())
        }
        viewModel.station.observe(viewLifecycleOwner) {
            stationAdapter = StationAdapter(
                requireContext(), R.layout.item_saleparty, it.orEmpty()
            )
            stationData = it
            binding.etStation.threshold = 1
            binding.etStation.setAdapter(stationAdapter)
            binding.etStation.setOnItemClickListener { parent, _, position, _ ->
                val stationId = stationAdapter.getItem(position)
                bookingStationId = stationId?.stationId.toString()
                binding.tilStation.isErrorEnabled =
                    !(stationId?.stationId.isNotNullOrBlank() || binding.tilStation.isErrorEnabled)

            }
        }


        viewModel.purchaseParty.observe(viewLifecycleOwner) {
            purchasePartyData = it?.filterNonEmptyFields()

            if (!::purchasePartyAdapter.isInitialized) {

                purchasePartyAdapter = PurchasePartyAdapter(
                    isNickNameSelected,
                    requireContext(),
                    R.layout.item_saleparty,
                    purchasePartyData.orEmpty()
                )

                binding.etPurchaseParty.threshold = 1
                binding.etPurchaseParty.setAdapter(purchasePartyAdapter)

            } else {
                if (isNickNameSelected) {
                    purchasePartyAdapter.updateType(true)
                    purchasePartyAdapter.updateData(purchasePartyData.orEmpty())
                } else {
                    purchasePartyAdapter.updateType(false)
                    purchasePartyAdapter.updateData(purchasePartyData.orEmpty())
                }
            }

            binding.etPurchaseParty.setOnItemClickListener { parent, v, position, l ->

                // val purPartyItem = parent.getItemAtPosition(position)  as PurchasePartyData
                val purPartyItem = parent.getItemAtPosition(position) as PurchasePartyData

                Log.i("TaG", "selected purchase party --------->${purPartyItem}")
                purchasePartyId = purPartyItem.id.toString()
                addItemViewModel.getItemsList(purchasePartyId)
                binding.etPurchaseParty.clearFocus()
                binding.etPurchaseParty.hideKeyBoard()
                binding.tilPurchaseParty.isErrorEnabled =
                    !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchaseParty.isErrorEnabled)
            }
        }

        viewModel.scheme.observe(viewLifecycleOwner) {
            schemeAdapter = SchemeAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            schemeData = it
            binding.etScheme.threshold = 1
            binding.etScheme.setAdapter(schemeAdapter)
            binding.etScheme.setOnItemClickListener { parent, _, position, _ ->
                val schemeItem = schemeAdapter.getItem(position)
                schemeId = schemeItem.schemeId.toString()
                binding.etPurchaseParty.text.clear()
                viewModel.getPurchaseParty(schemeItem.schemeId, true)

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
        checkCleanInValidSelectedData()
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
        } else if (etPurchaseParty.text.isBlank()) {
            etPurchaseParty.requestFocus()
            tilPurchaseParty.isErrorEnabled = true
            tilPurchaseParty.setError("You need to select purchase party")
            return false
        } else if (tvDispatchFromDate.text.isBlank()) {
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

}


