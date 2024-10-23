package com.ssspvtltd.quick_new.ui.order.add.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.BaseFragment
import com.ssspvtltd.quick_new.base.InflateF
import com.ssspvtltd.quick_new.databinding.FragmentAddOrderBinding
import com.ssspvtltd.quick_new.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick_new.model.ARG_ADD_ITEM_LIST
import com.ssspvtltd.quick_new.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick_new.model.order.add.additem.PackType
import com.ssspvtltd.quick_new.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick_new.ui.order.add.activity.AddImageActivity
import com.ssspvtltd.quick_new.ui.order.add.adapter.DefaultTransportAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.PackDataAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.PurchasePartyAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.SalePartyAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.SchemeAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.StationAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.SubPartyAdapter
import com.ssspvtltd.quick_new.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick_new.ui.order.add.viewmodel.AddOrderViewModel
import com.ssspvtltd.quick_new.utils.extension.getParcelableArrayListExt
import com.ssspvtltd.quick_new.utils.extension.getViewModel
import com.ssspvtltd.quick_new.utils.extension.isNotNullOrBlank
import com.ssspvtltd.quick_new.utils.showWarningDialog
import dagger.hilt.android.AndroidEntryPoint
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

    private var salePartyId: String         = ""
    private var subPartyId: String          = "SELF"
    private var purchasePartyId: String     = ""
    private var pvtMarka: String            = "*"
    private var bookingStationId: String    = ""
    private var transportId: String         = ""
    private var schemeId: String            = ""
    private var selectedStatus: String      = "PENDING"

    private val dateFormat  = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    private val addItemAdapter by lazy { PackDataAdapter() }

    companion object {
        const val TAG = "TaG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(1000)
            viewModel.initAllData()
            addItemViewModel.getPackDataList()
            addItemViewModel.getPackType()
            viewModel.getEditOrderDataIfNeeded()
        }

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()

        val dateFormat          = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate           = dateFormat.format(Calendar.getInstance().time)
        val threeDaysLaterDate  = dateFormat.format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.time)

        binding.tvDispatchFromDate.text = todayDate
        binding.tvDispatchToDate.text   = threeDaysLaterDate

        val statusOptions = listOf("PENDING","HOLD")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions)
        binding.autoCompleteStatus.setAdapter(adapter)

        binding.autoCompleteStatus.setOnItemClickListener { parent, view, position, id ->
            selectedStatus = parent.getItemAtPosition(position).toString()
           Log.i("TaG","Selected Status -=-=--=-=-> $selectedStatus")
        }
        binding.autoCompleteStatus.setText("PENDING", false)

        binding.toolbar.apply {
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
            if(viewModel.pendingOrderID.isNotNullOrBlank()) setTitle("Edit Order")
            else setTitle("Add Order")
        }
        binding.tvDispatchFromDate.setOnClickListener {
            // openSpinnerBirthdayDialog(true, false)
            // fromDatePicker()
            showFromDatePicker()
        }

        binding.tvDispatchToDate.setOnClickListener {
            //toDatePicker()
            showToDatePicker()
        }

        binding.etSalePartyName.doAfterTextChanged {
            salePartyId = ""
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
        binding.etScheme.doAfterTextChanged {
            schemeId = ""
            binding.etPurchaseParty.text.clear()
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
                val hashMap: HashMap<String, RequestBody?> = HashMap()
                hashMap["SalePartyId"] = salePartyId.toRequestBody()
                hashMap["SubPartyId"] = subPartyId.toRequestBody()
                hashMap["SubPartyasRemark"] = etSubPartyRemark.text.toString().toRequestBody()
                hashMap["PurchasePartyId"] = purchasePartyId.toRequestBody()
                hashMap["TransportId"] = transportId.toRequestBody()
                hashMap["BstationId"] = bookingStationId.toRequestBody()
                hashMap["SchemeId"] = schemeId.toRequestBody()
                hashMap["PvtMarka"] = pvtMarka.toRequestBody()
                hashMap["DeliveryDateFrom"] = tvDispatchFromDate.text.toString().toRequestBody()
                hashMap["DeliveryDateTo"] = tvDispatchToDate.text.toString().toRequestBody()
                hashMap["Remark"] = etDiscription.text.toString().toRequestBody()
                hashMap["TotalQty"] = "0".toRequestBody()
                hashMap["TotalAmt"] = "0".toRequestBody()
                hashMap["OrderTypeName"] = "TRADING".toRequestBody()
                hashMap["OrderStatus"] = selectedStatus.toRequestBody()


                viewModel.placeOrder(hashMap)
            }
        }
        binding.tvAddItem.setOnClickListener {
            if(binding.etPurchaseParty.text.isNullOrBlank()) {
                // binding.etPurchaseParty.requestFocus()
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
                    viewModel.purchaseParty.observe(viewLifecycleOwner) {
                        val purchasePartyAdapter = PurchasePartyAdapter(
                            true,
                            requireContext(),
                            R.layout.item_saleparty,
                            it?.distinctBy {it2-> it2.nickName }.orEmpty()
                        )
                        binding.etPurchaseParty.setThreshold(1)
                        binding.etPurchaseParty.setAdapter(purchasePartyAdapter)

                    }
                }

                R.id.radioBySupplierName -> {
                    binding.etPurchaseParty.text.clear()
                    viewModel.purchaseParty.observe(viewLifecycleOwner) {
                        val purchasePartyAdapter = PurchasePartyAdapter(
                            false, requireContext(), R.layout.item_saleparty, it.orEmpty()
                        )
                        binding.etPurchaseParty.setThreshold(1)
                        binding.etPurchaseParty.setAdapter(purchasePartyAdapter)
                    }
                }
            }
        }
        binding.recyclerViewAddItem.adapter = addItemAdapter
        addItemAdapter.onEditClick = { openAddBottomSheet(addItemViewModel.packTypeDataList.indexOf(it)) }
        addItemAdapter.onDeleteClick = { addItemViewModel.deletePackTypeData(it) }

        binding.etSalePartyName.setOnFocusChangeListener { v, hasFocus ->
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

    private fun openAddBottomSheet(index: Int = -1) {
        addItemViewModel.bottomSheetIndex = index
        AddItemBottomSheetFragment.newInstance()
            .show(childFragmentManager, AddItemBottomSheetFragment::class.simpleName)
    }

    private fun registerObserver() {
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
                    val hashMap: HashMap<String, RequestBody?> = HashMap()
                    hashMap["SalePartyId"]          = salePartyId.toRequestBody()
                    hashMap["SubPartyId"]           = subPartyId.toRequestBody()
                    hashMap["SubPartyasRemark"]     = etSubPartyRemark.text.toString().toRequestBody()
                    hashMap["PurchasePartyId"]      = purchasePartyId.toRequestBody()
                    hashMap["TransportId"]          = transportId.toRequestBody()
                    hashMap["BstationId"]           = bookingStationId.toRequestBody()
                    hashMap["SchemeId"]             = schemeId.toRequestBody()
                    hashMap["PvtMarka"]             = pvtMarka.toRequestBody()
                    hashMap["DeliveryDateFrom"]     = tvDispatchFromDate.text.toString().toRequestBody()
                    hashMap["DeliveryDateTo"]       = tvDispatchToDate.text.toString().toRequestBody()
                    hashMap["Remark"]               = etDiscription.text.toString().toRequestBody()
                    hashMap["TotalQty"]             = "0".toRequestBody()
                    hashMap["TotalAmt"]             = "0".toRequestBody()
                    hashMap["OrderTypeName"]        = "TRADING".toRequestBody()
                    hashMap["OrderStatus"]          = "HOLD".toRequestBody()

                    viewModel.placeOrder(hashMap)

                }
                dialog.dismissWithAnimation()
            }
        }
        viewModel.editOrderData.observe(viewLifecycleOwner) {
            binding.etSerialNo.setText(it?.orderNo)
            binding.etMarketerCode.setText(it?.marketerCode)
            binding.etSalePartyName.setText(it?.salePartyName)
            binding.etAvailableLimit.setText("1000")
            binding.etAverageDays.setText("10")
            binding.etSubParty.setText(it?.subPartyName)
            binding.etTransport.setText(it?.transportName)
            binding.etStation.setText(it?.bstationName)
            binding.etScheme.setText(it?.schemeName)
            binding.etPurchaseParty.setText(it?.purchasePartyName)
            binding.tvDispatchToDate.text = it?.deliveryDateTo
            binding.tvDispatchFromDate.text = it?.deliveryDateFrom
            binding.etDiscription.setText(it?.remark)

            val tempItemDetailList = mutableListOf<PackType>()
            it?.itemDetailsList?.forEach { itemDetail ->

                val tempSubItemList = mutableListOf<PackTypeItem>()
                itemDetail?.itemDetail?.forEach { subItem ->
                    tempSubItemList.add(
                        PackTypeItem(
                            itemID = subItem?.itemId,
                            itemName = subItem?.itemName,
                            itemQuantity = (subItem?.itemQuantity ?: 0).toString()
                        )
                    )
                }

                tempItemDetailList.add(
                    PackType(
                        pcsId = itemDetail?.pcsId ?: "",
                        packName = itemDetail?.packName,
                        qty = itemDetail?.qty.toString(),
                        amount = itemDetail?.amount.toString(),
                        itemDetail = tempSubItemList.toList()
                    )
                )
            }


            viewModel.addItemDataList = ArrayList(tempItemDetailList)
            addItemViewModel.packTypeDataList = tempItemDetailList
            addItemViewModel.getPackDataList()
        }

        viewModel.voucherData.observe(viewLifecycleOwner) {
            binding.etMarketerCode.setText(it?.voucherCode)
            binding.etSerialNo.setText(it?.voucherNO)
        }

        viewModel.saleParty.observe(viewLifecycleOwner) {
            val salePartyAdapter = SalePartyAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            binding.etSalePartyName.setThreshold(1)
            binding.etSalePartyName.setAdapter(salePartyAdapter)
            binding.etSalePartyName.setOnItemClickListener { parent, _, position, _ ->
                val salePartyItem = salePartyAdapter.getItem(position)
                salePartyId = salePartyItem.accountID.toString()
                viewModel.getSalePartyDetails(salePartyId)
                binding.tilSaletParty.isErrorEnabled = !(salePartyItem.accountName.isNotNullOrBlank() || binding.tilSaletParty.isErrorEnabled)
                viewModel.getStation(salePartyId, "")
            }

        }

        viewModel.salePartyDetail.observe(viewLifecycleOwner) {
            val subPartyAdapter = SubPartyAdapter(
                requireContext(), R.layout.item_saleparty, it?.subPartyList ?: emptyList()
            )
            binding.etSubParty.setThreshold(1)
            binding.etSubParty.setAdapter(subPartyAdapter)
            binding.etSubParty.setOnItemClickListener { parent, _, position, _ ->
                val subPartyItem = subPartyAdapter.getItem(position)
                subPartyId = subPartyItem.subPartyId
                binding.tilSubParty.isErrorEnabled = !(subPartyItem.subPartyId.isNotNullOrBlank() || binding.tilSubParty.isErrorEnabled)
                viewModel.getStation(salePartyId, subPartyId)
            }
            val defaultTransportAdapter = DefaultTransportAdapter(
                requireContext(), R.layout.item_saleparty, it?.defTransport.orEmpty()
            )

            binding.etTransport.setThreshold(1)
            binding.etTransport.setAdapter(defaultTransportAdapter)
            binding.etTransport.setOnItemClickListener { parent, _, position, _ ->
                val tId = defaultTransportAdapter.getItem(position)
                transportId = tId?.transportId.toString()
                binding.tilTransport.isErrorEnabled = !(tId?.transportId.isNotNullOrBlank() || binding.tilTransport.isErrorEnabled)
            }

            binding.etAvailableLimit.setText(it?.avlLimit.toString())
            binding.etAverageDays.setText(it?.avgDays.toString())
            binding.etAverageDays.setText(it?.avgDays.toString())
            binding.etSubParty.setText(it?.defSubPartyName.orEmpty())
            binding.etStation.setText(it?.defTransport?.get(0)?.defStation?.get(0)?.stationName.orEmpty())
            bookingStationId = it?.defTransport?.get(0)?.defStation?.get(0)?.stationId.toString()
            binding.etTransport.setText(it?.defTransport?.get(0)?.transportName)
            transportId = (it?.defTransport?.get(0)?.transportId.toString())
        }
        viewModel.station.observe(viewLifecycleOwner) {
            val stationAdapter = StationAdapter(
                requireContext(), R.layout.item_saleparty, it.orEmpty()
            )
            binding.etStation.setThreshold(1)
            binding.etStation.setAdapter(stationAdapter)
            binding.etStation.setOnItemClickListener { parent, _, position, _ ->
                val stationId = stationAdapter.getItem(position)
                bookingStationId = stationId?.stationId.toString()
                binding.tilStation.isErrorEnabled = !(stationId?.stationId.isNotNullOrBlank() || binding.tilStation.isErrorEnabled)

            }
        }

        viewModel.purchaseParty.observe(viewLifecycleOwner) {
            val purchasePartyAdapter = PurchasePartyAdapter(
                true,
                requireContext(),
                R.layout.item_saleparty,
                it?.distinctBy {it2 ->  it2.nickName }.orEmpty()
            )
            binding.etPurchaseParty.setThreshold(1)
            binding.etPurchaseParty.setAdapter(purchasePartyAdapter)
            binding.etPurchaseParty.setOnItemClickListener { parent, _, position, _ ->
                val purPartyItem = purchasePartyAdapter.getItem(position)
                purchasePartyId = purPartyItem.id.toString()
                addItemViewModel.getItemsList(purchasePartyId)
                binding.tilPurchaseParty.isErrorEnabled = !(purPartyItem.id.isNotNullOrBlank() || binding.tilPurchaseParty.isErrorEnabled)
            }
        }

        viewModel.scheme.observe(viewLifecycleOwner) {
            val schemeAdapter =
                SchemeAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            binding.etScheme.setThreshold(1)
            binding.etScheme.setAdapter(schemeAdapter)
            binding.etScheme.setOnItemClickListener { parent, _, position, _ ->
                val schemeItem = schemeAdapter.getItem(position)
                schemeId = schemeItem.schemeId.toString()
                binding.etPurchaseParty.text.clear()
                viewModel.getPurchaseParty(schemeItem.schemeId)

                binding.tilScheme.isErrorEnabled = !(schemeItem.schemeId.isNotNullOrBlank() || binding.tilScheme.isErrorEnabled)
            }
        }

        addItemViewModel.isListAvailable.observe(viewLifecycleOwner) {
            addItemAdapter.submitList(addItemViewModel.widgetList)
            Log.i("TaG","packTypeDataList Set 2222-=-------->> ${addItemViewModel.packTypeDataList}")
            viewModel.addItemDataList = ArrayList(addItemViewModel.packTypeDataList)
            binding.tvAddItem.requestFocus()
            binding.tvAddItem.setBackgroundResource(R.drawable.gray_300_bg)
            binding.tvAddItem.error = null
        }
    }

    private fun validate(): Boolean = with(binding) {
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
        } else if (radioSubparty.isChecked && etSubParty.text.isBlank() && (!etSubParty.text.toString().equals("self", true))
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
        }else if (tvDispatchFromDate.text.isBlank()) {
            tvDispatchFromDate.requestFocus()
            tvDispatchFromDate.setBackgroundResource(R.drawable.red_outline)
            tvDispatchFromDate.error = "Please enter from date"
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
            if(viewModel.addImageDataList.size > 0) {
                binding.llImageCount.visibility = View.VISIBLE
                binding.tvImageCount.text       = checkTwoDigitNo(viewModel.addImageDataList.size.toString())
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

        val fromDateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            fromDate.set(Calendar.YEAR, year)
            fromDate.set(Calendar.MONTH, monthOfYear)
            fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.tvDispatchFromDate.text = dateFormat.format(fromDate.time)
            // Check if ToDate needs to be updated based on the selected FromDate
            val currentToDate = Calendar.getInstance().apply {
                time = dateFormat.parse(binding.tvDispatchToDate.text.toString())!!
            }
            if (currentToDate.before(fromDate)) {
                val updatedToDate = fromDate.clone() as Calendar
                updatedToDate.add(Calendar.DAY_OF_YEAR, 0)
                binding.tvDispatchToDate.text = dateFormat.format(updatedToDate.time)
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), fromDateListener, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH)
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

        val toDateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            toDate.set(Calendar.YEAR, year)
            toDate.set(Calendar.MONTH, monthOfYear)
            toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.tvDispatchToDate.text = dateFormat.format(toDate.time)
        }

        val fromDate = Calendar.getInstance().apply {
            time = dateFormat.parse(binding.tvDispatchFromDate.text.toString())!!
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(), toDateListener, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict the ToDate to be at least 3 days after fromDate, and at most 3 months from today's date
        datePickerDialog.datePicker.minDate = fromDate.timeInMillis + (1 * 24 * 60 * 60 * 1000)  // +3 days from fromDate
        val maxDate = Calendar.getInstance().apply { add(Calendar.MONTH, 3) }.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()
    }

}


