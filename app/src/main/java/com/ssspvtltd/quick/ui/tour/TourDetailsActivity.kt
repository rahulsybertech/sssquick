package com.ssspvtltd.quick.ui.tour

import android.Manifest
import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityTourDetailsBinding
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.ui.order.add.adapter.AddImageAdapter
import com.ssspvtltd.quick.ui.tour.adapter.CategoryAdapter
import com.ssspvtltd.quick.ui.tour.adapter.FirmAdapter
import com.ssspvtltd.quick.ui.tour.adapter.GradeAdapter
import com.ssspvtltd.quick.ui.tour.adapter.ImageAdapter
import com.ssspvtltd.quick.ui.tour.adapter.LeadResourceAdapter
import com.ssspvtltd.quick.ui.tour.adapter.StateAdapter
import com.ssspvtltd.quick.ui.tour.adapter.StationAdapter
import com.ssspvtltd.quick.ui.tour.adapter.TourCategoryAdapter
import com.ssspvtltd.quick.ui.tour.model.CategoryItem
import com.ssspvtltd.quick.ui.tour.model.GradeItem
import com.ssspvtltd.quick.ui.tour.model.ImageItem
import com.ssspvtltd.quick.ui.tour.model.LeadDetails
import com.ssspvtltd.quick.ui.tour.model.LeadRequest
import com.ssspvtltd.quick.ui.tour.model.LeadResource
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryItem
import com.ssspvtltd.quick.ui.tour.model.ShopCategoryRequest
import com.ssspvtltd.quick.ui.tour.model.StateItem
import com.ssspvtltd.quick.ui.tour.model.StationItem
import com.ssspvtltd.quick.ui.tour.viewmodel.TourDetailsViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TourDetailsActivity : BaseActivity<ActivityTourDetailsBinding, TourDetailsViewModel>() {
    private var imageUri: Uri? = null
    private val mAdapter by lazy { AddImageAdapter() }
    private var isGradeApiCalled = false
    private var isStationApiCalled = false
    private var isLeadResourceApiCalled = false
    private var isStateApiCalled = false
    private var isCategoryApiCalled = false
    var customerData: List<AccountName> = emptyList()
    var gradeData: List<GradeItem> = emptyList()
    var stationData: List<StationItem> = emptyList()
    var leadResourceData: List<LeadResource> = emptyList()
    var stateData: List<StateItem> = emptyList()
    var categoryData: List<CategoryItem> = emptyList()
    var ShopCategory: List<ShopCategoryItem> = emptyList()
    private var isNextScreen = false
    private lateinit var adapter: CategoryAdapter
    private var isAllSelected = false
    private var selectedImageType = ""
    private val selfieList =
        mutableListOf<ImageItem>()

    private val bottomList =
        mutableListOf<ImageItem>()
    private lateinit var selfieAdapter: ImageAdapter
    private lateinit var bottomAdapter: ImageAdapter
    private var selectedFirmName: String? = null
    private var selectedFirmId: String? = null
    private var selectedGradeName: String? = null
    private var selectedGradeId: String? = null
    private var selectedStateName: String? = null

    private var selectedStateId: String? = null
    private var selectedLeadResocureId: String? = null
    private var selectedLeadResocureName: String? = null
    private var selectedStationName: String? = null
    private var selectedStationId: String? = null
    private var ownerName: String? = null
    private var mobileNumber: String? = null
    private var whatppMobileNumber: String? = null
    private var selectedCategoryName: String? = null
    private var selectedCategoryId: String? = null
    var latitude: Double? = null
    var longitude : Double?=null
    private var leadId: String? = null
    var mobileNo: String? = null
    var  customerType="existing"
    private lateinit var fusedLocationClient: FusedLocationProviderClient





    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            bitmap?.let {

                if (selectedImageType == "selfie") {

                    if (selfieList.size < 2) {

                        selfieList.add(ImageItem(bitmap = bitmap))

                        selfieAdapter.notifyDataSetChanged()
                    }

                } else {

                    if (bottomList.size < 5) {

                        bottomList.add(ImageItem(bitmap = bitmap))

                        bottomAdapter.notifyDataSetChanged()
                    }
                }
            }
        }



    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            uri?.let {

                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                )

                if (selectedImageType == "selfie") {

                    if (selfieList.size < 2) {

                        selfieList.add(ImageItem(bitmap = bitmap))

                        selfieAdapter.notifyDataSetChanged()
                    }

                } else {

                    if (bottomList.size < 5) {

                        bottomList.add(ImageItem(bitmap = bitmap))

                        bottomAdapter.notifyDataSetChanged()
                    }
                }
            }

        }

    private val pickContact =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data ?: return@registerForActivityResult

                val cursor = contentResolver.query(
                    uri,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    null,
                    null,
                    null
                )

                cursor?.use {

                    if (it.moveToFirst()) {

                        val numberIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )

                        if (numberIndex != -1) {

                            val number = it.getString(numberIndex)

                            Log.d("CONTACT", "NUMBER = $number")

                            val cleanNumber = number
                                ?.replace("\\s".toRegex(), "")
                                ?.replace("+91", "")
                                ?.replace("-", "")

                            binding.mobileNo1.setText(cleanNumber)
                        }
                    }
                }
            }
        }



    override val inflate: InflateA<ActivityTourDetailsBinding> get() = ActivityTourDetailsBinding::inflate
    override fun initViewModel(): TourDetailsViewModel = getViewModel()
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {

            if (doubleBackToExitPressedOnce) {
                finish()
            } else {
                doubleBackToExitPressedOnce = true

                Toast.makeText(
                    this@TourDetailsActivity,
                    "Press back again to exit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        leadId = intent.getStringExtra("leadId")
        checkEditMode()
        initViews()
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
     //   registerObserver()
      //  registerListener()
        setupFirmDropdown()
        setupGradeDropdown()
        setupStationDropdown()
        if (!isLeadResourceApiCalled) {
            isLeadResourceApiCalled = true
            viewModel.getleadResourceList()
        }
        setupLeadResourceDropdown()

      //  setupStateDropdown()
        stateList()
        setupCategoryeDropdown()
        setupSubCategory()

        binding.firmNameNewCustomer.doOnTextChanged { text, _, _, _ ->

            if (!text.isNullOrBlank()) {
                binding.layouNewCustomer.isErrorEnabled = false
            }
        }
        binding.mobileNo2.setOnFocusChangeListener { view, hasFocus ->
        /*    if (hasFocus) {

                binding.nestedScrollView.post {
                    binding.nestedScrollView.smoothScrollTo(
                        0,
                        view.top
                    )
                }
            }*/
        }

        binding.shopArea.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {

                binding.nestedScrollView.post {
                    binding.nestedScrollView.smoothScrollTo(
                        0,
                        view.top
                    )
                }
            }
        }


        binding.btnNext.setOnClickListener {

            if (!isNextScreen) {

                // FIRST SCREEN → NEXT

                if(validateForm()){


                  /*  binding.edtOldAgent.isFocusableInTouchMode = true
                    binding.edtOldAgent.requestFocus()*/
                    isNextScreen = true

                    binding.btnNext.text = "Save"

                    binding.btnBack.visibility = View.VISIBLE

                    binding.mainLayout.visibility = View.GONE
                    binding.radioGroupCustomer.visibility = View.GONE

                    binding.layoutStepTwo.visibility = View.VISIBLE

                }


            } else {

                // SECOND SCREEN → SAVE


                if (validateForm()) {

                    val leadSource = binding.dropLeadSource.text.toString().trim()

                    if (leadSource == "MARKETER VISIT") {

                        if (selfieList.isEmpty()) {
                            showToast("At least one selfie image is required")
                            return@setOnClickListener
                        }

                        if (bottomList.isEmpty()) {
                            showToast("At least one shop image is required")
                            return@setOnClickListener
                        }
                    }

                    saveData()
                }


                viewModel.submitLeadResponse.observe(this) { response ->

                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (response.success) {
                        Log.e("TAG", "saveData: $response")
                        finish()
                        // success work
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener {
            if (isNextScreen) {

                // STEP 2 → STEP 1
                isNextScreen = false

                binding.btnNext.text = "Next"

                binding.btnBack.visibility = View.GONE

                binding.mainLayout.visibility = View.VISIBLE
                binding.radioGroupCustomer.visibility = View.VISIBLE

                binding.layoutStepTwo.visibility = View.GONE

            } else {

                // STEP 1 → Finish Activity
                finish()
            }
        }

        binding.btnBack.setOnClickListener {

            isNextScreen = false

            binding.btnNext.text = "Next"

            binding.btnBack.visibility = View.GONE

            binding.mainLayout.visibility = View.VISIBLE
            binding.radioGroupCustomer.visibility = View.VISIBLE

            binding.layoutStepTwo.visibility = View.GONE
        }



        viewModel.getleadDetailByLedId.observe(this) { data ->

            data?.let {

                setLeadData(it)
            }
        }
      /*  binding.ownerName.filters = arrayOf(
            InputFilter.LengthFilter(50),
            InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    val ch = source[i]
                    if (!ch.isLetter() && ch != ' ') {
                        return@InputFilter ""
                    }
                }
                null
            }
        )*/

        /*binding.ownerName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val filteredText = text.replace("\\s{2,}".toRegex(), " ")

                if (text != filteredText) {
                    binding.ownerName.removeTextChangedListener(this)
                    binding.ownerName.setText(filteredText)
                    binding.ownerName.setSelection(filteredText.length)
                    binding.ownerName.addTextChangedListener(this)
                }
            }
        })*/
        binding.shopArea.filters = arrayOf(
            InputFilter.LengthFilter(10),
         /*   InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    val ch = source[i]
                    if (!ch.isLetter() && ch != ' ') {
                        return@InputFilter ""
                    }
                }
                null
            }*/
        )
        binding.edtOldAgent.filters = arrayOf(
            InputFilter.LengthFilter(50),
        )

        binding.edtOldAgent.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.edtYearlySale.requestFocus()
                true
            } else {
                false
            }
        }
        binding.edtYearlySale.filters = arrayOf(
            InputFilter.LengthFilter(10),
        )

        binding.mobileNo1.doAfterTextChanged { text ->

            val mobile = text.toString().trim()

            binding.layoutMobileNum.error =
                if (mobile.matches(Regex("^[6-9][0-9]{9}$"))) null
                else binding.layoutMobileNum.error
        }

    }
    private fun checkLocationPermission() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getCurrentLocation()

        } else {
             val locationPermissionRequest =
                registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->

                    val fineLocationGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

                    val coarseLocationGranted =
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

                    if (fineLocationGranted || coarseLocationGranted) {
                        getCurrentLocation()
                    } else {
                        Toast.makeText(
                            this,
                            "Location permission denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    private fun getCurrentLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                location?.let {

                     latitude = it.latitude
                     longitude = it.longitude

                    Log.e("TAG", "Latitude: $latitude")
                    Log.e("TAG", "Longitude: $longitude")
                    binding.txtLatLong.text = "Lat, Long: $latitude, $longitude"
                    getAddressFromLatLng(latitude,longitude)
                }
            }
    }


    private fun getAddressFromLatLng(
        latitude: Double?,
        longitude: Double?
    ) {

        try {

            val geocoder = Geocoder(
                this,
                Locale.getDefault()
            )

            val addresses =
                geocoder.getFromLocation(latitude!!, longitude!!, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0)

                val city = address.locality

                val state = address.adminArea
                val country = address.countryName
                val pinCode = address.postalCode
                Log.e("TAG", "Full Address: $fullAddress")
                Log.e("TAG", "City: $city")
                Log.e("TAG", "State: $state")
                Log.e("TAG", "Country: $country")
                Log.e("TAG", "PinCode: $pinCode")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLeadData(data: LeadDetails) = with(binding) {
        if (data.accountID.isNullOrEmpty()) {

            // NEW CUSTOMER

            binding.rbNew.isChecked = true

            customerType = "new"

            binding.layouNewCustomer.visibility =
                View.VISIBLE
            binding.layouLeadSource.visibility =
                View.VISIBLE

            binding.layouCustomer.visibility =
                View.GONE

            binding.firmNameNewCustomer.setText(
                data.firmName ?: ""
            )
            // All fields editable
            setFieldsEditable(true)
            binding.layouGrade.endIconMode =
                TextInputLayout.END_ICON_DROPDOWN_MENU

        } else {

            // EXISTING CUSTOMER

            binding.rbExisting.isChecked = true
            binding.layouGrade.endIconMode =
                TextInputLayout.END_ICON_CLEAR_TEXT

            customerType = "existing"

            binding.layouCustomer.visibility =
                View.VISIBLE

            binding.layouNewCustomer.visibility =
                View.GONE
            binding.layouLeadSource.visibility =
                View.GONE

            binding.dropCity.setText(
                data.firmName ?: "",
                false
            )

            selectedFirmId = data.accountID
            // All fields editable
            setFieldsEditable(false)
        }
        dropCity.setText(data.firmName ?: "", false)
        leadId=data.id
       // selectedFirmId=data.fi

        dropGrade.setText(data.gradeName ?: "", false)
        selectedGradeId=data.gradeID

        dropStation.setText(data.stationName ?: "", false)
        selectedStationId=data.stationId


        dropState.setText(data.stateName ?: "", false)
        selectedStateId=data.stateId
        dropLeadSource.setText(data.leadTypeName ?: "", false)
        selectedLeadResocureId=data.leadTypeId

        ownerName.setText(data.ownerName ?: "", false)

        mobileNo1.setText(data.mobileNo ?: "")

        mobileNo2.setText(data.whatsappNo ?: "")

        dropCategory.setText(data.categoryName ?: "", false)
        selectedCategoryId=data.categoryId

        shopArea.setText(data.shopArea ?: "")

        workingBranchs.setText(data.workingBranch ?: "")

        edtOldAgent.setText(data.oldAgentName ?: "")
      /*  Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imgPhoto)*/

        data.selfieImageURL1?.let {

            selfieList.add(
                ImageItem(imageUrl = it)
            )
        }

        data.selfieImageURL2?.let {

            selfieList.add(
                ImageItem(imageUrl = it)
            )
        }



        data.shopImageURL1?.let {

            bottomList.add(
                ImageItem(imageUrl = it)
            )
        }

        data.shopImageURL2?.let {

            bottomList.add(
                ImageItem(imageUrl = it)
            )
        }

        data.shopImageURL3?.let {

            bottomList.add(
                ImageItem(imageUrl = it)
            )
        }

        data.shopImageURL4?.let {

            bottomList.add(
                ImageItem(imageUrl = it)
            )
        }

        data.shopImageURL5?.let {

            bottomList.add(
                ImageItem(imageUrl = it)
            )
        }



        selfieAdapter.notifyDataSetChanged()

        bottomAdapter.notifyDataSetChanged()

        edtYearlySale.setText(data.yearlySale ?: "")

        edtRemarks.setText(data.remark ?: "")

        txtLatLong.text =
            "Long, Lat: ${data.longitude}, ${data.latitude}"



        // SHOP CATEGORY SELECT

        val selectedCategories =
            data.shopCategorys
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList()



        ShopCategory.forEach { item ->

            item.isSelected =
                selectedCategories.any { selected ->

                    selected.equals(
                        item.name,
                        ignoreCase = true
                    )
                }
        }




       // adapter.notifyDataSetChanged()
    }

    private fun setFieldsEditable(isEditable: Boolean) = with(binding) {


       // dropCity.isEnabled = isEditable
        dropGrade.isEnabled = isEditable
        dropStation.isEnabled = isEditable
        dropState.isEnabled = isEditable
        ownerName.isEnabled = isEditable

        mobileNo1.isEnabled = isEditable
        mobileNo2.isEnabled = isEditable

        dropCategory.isEnabled = isEditable
        shopArea.isEnabled = isEditable
        workingBranchs.isEnabled = isEditable
        edtOldAgent.isEnabled = isEditable
        edtYearlySale.isEnabled = isEditable

        firmNameNewCustomer.isEnabled = isEditable





     //   txtYearlySale.alpha = 0.7f

        // Only remarks always editable
        edtRemarks.isEnabled = true
        edtRemarks.isEnabled = true

        if (::adapter.isInitialized) {
            adapter.isCategoryEditable = isEditable
            adapter.notifyDataSetChanged()
        }

        if (!isEditable) {
            recyclerCategory.alpha = 0.7f
            edtOldAgent.alpha = 0.7f
            binding.edtOldAgent.apply {
                isEnabled = false
                alpha = 0.5f
            }

            binding.txtOldAgent.alpha = 0.5f
            binding.edtYearlySale.apply {
                isEnabled = false
                alpha = 0.5f
            }

            binding.txtYearlySale.alpha = 0.5f
            binding.btnSelectAll.alpha = 0.5f
            btnSelectAll.isClickable=false
            binding.tvShopCategory.alpha = 0.5f

        } else {
            recyclerCategory.alpha = 1f
            recyclerCategory.alpha = 1f
            edtOldAgent.alpha = 1f
            binding.edtOldAgent.apply {
                isEnabled = true
                alpha = 1f
            }

            binding.txtOldAgent.alpha = 1f
            binding.edtYearlySale.apply {
                isEnabled = true
                alpha = 1f
            }

            binding.txtYearlySale.alpha = 1f
            binding.btnSelectAll.alpha = 1f
            btnSelectAll.isClickable=true
            binding.tvShopCategory.alpha = 1f

        }

        // Image buttons/layouts always editable
        //btnSelfie.isEnabled = true
      //  btnShopImage.isEnabled = true
    }
    private fun clearForm() = with(binding) {

        dropCity.setText("", false)
        dropGrade.setText("", false)
        dropStation.setText("", false)
        dropState.setText("", false)
        dropCategory.setText("", false)

        ownerName.setText("")
        mobileNo1.setText("")
        mobileNo2.setText("")
        shopArea.setText("")
        workingBranchs.setText("")
        edtOldAgent.setText("")
        edtYearlySale.setText("")
        edtRemarks.setText("")
        firmNameNewCustomer.setText("")

        txtLatLong.text = ""

        // Clear IDs
        leadId = ""
        selectedFirmId = ""
        selectedGradeId = ""
        selectedStationId = ""
        selectedStateId = ""
        selectedCategoryId = ""

        // Clear images
        selfieList.clear()
        bottomList.clear()

        selfieAdapter.notifyDataSetChanged()
        bottomAdapter.notifyDataSetChanged()

        // Clear shop categories
        ShopCategory.forEach {
            it.isSelected = false
        }
     //   adapter.notifyDataSetChanged()
    }
    private fun checkEditMode() {

        if (!leadId.isNullOrEmpty()) {

            // EDIT MODE
            binding.txtTitle.setText("Edit Lead")
            viewModel.getLeadDetailByLeadID(leadId!!)
            setCustomerTypeEnabled(false)

        } else {

            // ADD MODE
            setCustomerTypeEnabled(true)
            binding.txtTitle.setText("Add Lead")
        }
    }

    private fun setCustomerTypeEnabled(enabled: Boolean) {
        binding.rbExisting.isEnabled = enabled
        binding.rbNew.isEnabled = enabled
    }
    private fun validateForm(): Boolean {

        // Existing Customer validation
        if (binding.rbExisting.isChecked) {

            if (selectedFirmId!!.isBlank()) {
               // binding.layouCustomer.error = "Select Firm Name"
                binding.dropCity.requestFocus()
                return false
            }

         /*   if (binding.dropGrade.text.isNullOrEmpty()) {
                binding.layouGrade.error = "Select Grade"
                return false
            }
*/
            if (binding.dropState.text.isNullOrEmpty()) {
                binding.layouState.error = "Select State"
                return false
            }
            if (binding.dropStation.text.isNullOrEmpty()) {
                binding.layouStation.error = "Select Station"
                return false
            }



            if (binding.mobileNo1.text.isNullOrEmpty()) {
                binding.layoutMobileNum.error = "Enter Mobile No"
                return false
            }

            if (!binding.mobileNo1.text.toString()
                    .matches(Regex("^[6-9][0-9]{9}$"))
            ) {
                binding.mobileNo1.error = "Invalid Mobile Number"
                return false
            }

            if (binding.dropCategory.text.isNullOrEmpty()) {
                binding.layouCategory.error = "Select Category"
                binding.dropCategory.requestFocus()
                return false
            }
        /*    val area = binding.shopArea.text.toString().trim()

            if (area.isNotEmpty()) {
                val regex = Regex("^\\d{1,10}(\\s?sqft)?$", RegexOption.IGNORE_CASE)

                if (!regex.matches(area)) {
                    binding.shopArea.error = "Enter valid area (e.g. 4500 or 4500 sqft)"
                    return false
                }
            }*/


        }

        // New Customer validation
        else if (binding.rbNew.isChecked) {

            if (binding.firmNameNewCustomer.text.isNullOrEmpty()) {
            //    binding.layouNewCustomer.error = "Enter Firm Name"
                binding.layouNewCustomer.requestFocus()
                return false
            }

        /*    if (binding.dropGrade.text.isNullOrEmpty()) {
                binding.layouGrade.error = "Select Grade"
                return false
            }*/



            if (selectedStateId!!.isBlank()) {
                binding.layouState.requestFocus()
              //  binding.layouState.error = "Select State"
                return false
            }
            if (selectedStationId.isNullOrBlank()) {
                binding.layouStation.requestFocus()
              //  binding.layouStation.error = "Select Station"
                return false
            }
       /*     if (binding.dropStation.text.isNullOrEmpty()) {
                binding.layouStation.error = "Select Station"
                return false
            }*/

            if (binding.mobileNo1.text.isNullOrEmpty()) {
              //  binding.layoutMobileNum.error = "Enter Mobile No"
                binding.layoutMobileNum.requestFocus()
                return false
            }

            if (!binding.mobileNo1.text.toString()
                    .matches(Regex("^[6-9][0-9]{9}$"))
            ) {
                binding.layoutMobileNum.requestFocus()
               // binding.mobileNo1.error = "Invalid Mobile Number"
                return false
            }

            if (binding.dropLeadSource.text.toString() == "MARKETER VISIT") {
                if (selectedCategoryId.isNullOrEmpty()) {
                    // binding.layouCategory.error = "Select Category"
                    binding.dropCategory.requestFocus()
                    return false
                }
            }
      /*      if (!binding.dropLeadSource.text.toString().equals("TOUR VISIT")) {
                if (selectedCategoryId!!.isEmpty()){
                    showToast("select al-least one category")
                    return false
                }
                binding.dropCategory.requestFocus()
            }*/

         /*   if (binding.dropCategory.text.isNullOrEmpty()) {

                binding.layouCategory.error = "Select Category"
                binding.dropCategory.requestFocus()
                return false
            }*/

            val area = binding.shopArea.text.toString().trim()
/*

            if (area.isNotEmpty()) {
                val regex = Regex("^\\d{1,10}(\\s?sqft)?$", RegexOption.IGNORE_CASE)

                if (!regex.matches(area)) {
                    binding.shopArea.error = "Enter valid area (e.g. 4500 or 4500 sqft)"
                    return false
                }
            }*/
        }

        return true
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun saveData() {
         mobileNo = binding.mobileNo1.text.toString()
        val  mobileNo2 = binding.mobileNo2.text.toString()
       val edtRemarks = binding.edtRemarks.text.toString()
       val edtOldAgent = binding.edtOldAgent.text.toString()
       val edtYearlySale = binding.edtYearlySale.text.toString()
       val ownerName = binding.ownerName.text.toString()
       selectedGradeName = binding.dropGrade.text.toString()
       selectedStationName = binding.dropState.text.toString()
       selectedCategoryName = binding.dropCategory.text.toString()
       selectedLeadResocureName = binding.dropLeadSource.text.toString()
       val shopArea = binding.shopArea.text.toString()
       val workingBranchs = binding.workingBranchs.text.toString()
        val selectedCategoryList = adapter.getSelectedCategories()
        getCurrentLocation()
        var selfieImage1 = ""
        var selfieImageURL1 = ""

        if (selfieList.isNotEmpty()) {
            if (selfieList[0].bitmap != null) {
                selfieImage1 = bitmapToBase64(selfieList[0])
            } else {
                selfieImageURL1 = selfieList[0].imageUrl ?: ""
            }
        }
        var selfieImage2 = ""
        var selfieImageURL2 = ""

        if (selfieList.size > 1) {
            if (selfieList[1].bitmap != null) {
                selfieImage2 = bitmapToBase64(selfieList[1])
            } else {
                selfieImageURL2 = selfieList[1].imageUrl ?: ""
            }
        }




        var shopImage1 = ""
        var shopImageURL1 = ""

        var shopImage2 = ""
        var shopImageURL2 = ""

        var shopImage3 = ""
        var shopImageURL3 = ""

        var shopImage4 = ""
        var shopImageURL4 = ""

        var shopImage5 = ""
        var shopImageURL5 = ""

        if (bottomList.size > 0) {
            if (bottomList[0].bitmap != null) {
                shopImage1 = bitmapToBase64(bottomList[0])
            } else {
                shopImageURL1 = bottomList[0].imageUrl ?: ""
            }
        }

        if (bottomList.size > 1) {
            if (bottomList[1].bitmap != null) {
                shopImage2 = bitmapToBase64(bottomList[1])
            } else {
                shopImageURL2 = bottomList[1].imageUrl ?: ""
            }
        }

        if (bottomList.size > 2) {
            if (bottomList[2].bitmap != null) {
                shopImage3 = bitmapToBase64(bottomList[2])
            } else {
                shopImageURL3 = bottomList[2].imageUrl ?: ""
            }
        }

        if (bottomList.size > 3) {
            if (bottomList[3].bitmap != null) {
                shopImage4 = bitmapToBase64(bottomList[3])
            } else {
                shopImageURL4 = bottomList[3].imageUrl ?: ""
            }
        }

        if (bottomList.size > 4) {
            if (bottomList[4].bitmap != null) {
                shopImage5 = bitmapToBase64(bottomList[4])
            } else {
                shopImageURL5 = bottomList[4].imageUrl ?: ""
            }
        }
        val request = LeadRequest(
            id =
                if (leadId?.isNotEmpty() == true) {
                    leadId
                } else {
                    null
                },
            leadNo = 1,
            leadTypeId =   if (selectedLeadResocureId?.isNotEmpty() == true) {
                selectedLeadResocureId
            } else {
                null
            },

            leadTypeName = selectedLeadResocureName,

            date = getCurrentDateTime(),

            ownerName = ownerName,

            /*firmName = selectedFirmName ?: "",*/

            firmName =
                if (customerType == "existing") {
                selectedFirmName ?: ""
            } else {
                binding.firmNameNewCustomer.text.toString()
            }


            , mobileNo = mobileNo ?: "",

            whatsappNo = mobileNo2?: "",

            stateId = selectedStateId ?: "",

            stateName = selectedStateName ?: "",

            stationId = selectedStationId ?: "",

            stationName = selectedStationName ?: "",

            categoryId = if (selectedCategoryId?.isNotEmpty() == true) {
                selectedCategoryId
            } else {
                null
            },

            categoryName = selectedCategoryName ?: "",

            gradeID = if (selectedGradeId?.isNotEmpty() == true) {
                selectedGradeId
            } else {
                null
            },

            gradeName = selectedGradeName ?: "",

            oldAgentName = edtOldAgent,

            yearlySale = edtYearlySale,

            remark = edtRemarks,
            accountID =
                if (customerType == "existing") {
                    selectedFirmId
                } else {
                    null
                },

            companyId = null,

            latitude = latitude.toString() ?: "",

            longitude = longitude.toString() ?: "",

            selfieImage1 =selfieImage1,
            selfieImageURL1 =selfieImageURL1,

            selfieImage2 =selfieImage2,
            selfieImageURL2 =selfieImageURL2,


            shopImage1 = shopImage1,
            shopArea = shopArea,
            workingBranch = workingBranchs,
            shopImageURL1 = shopImageURL1,

            shopImage2 = shopImage2,
            shopImageURL2 = shopImageURL2,

            shopImage3 = shopImage3,
            shopImageURL3 = shopImageURL3,

            shopImage4 = shopImage4,
            shopImageURL4 = shopImageURL4,

            shopImage5 = shopImage5,
            shopImageURL5 = shopImageURL5,

            shopCategory = selectedCategoryList.map {

                ShopCategoryRequest(
                    id = it.id,
                    name = it.name
                )
            }
        )

       viewModel.submitLead(request)
        val gson = Gson()

        val json = gson.toJson(request)

        Log.d("REQUEST_JSON", json)
        Log.e("TAG", "saveData: $request")
    }

    private fun getCurrentDateTime(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        ).format(Date())
    }

    private fun initViews() = with(binding) {


        binding.radioGroupCustomer.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                binding.rbExisting.id -> {

                    customerType = "existing"

                    binding.dropCity.setText("")

                    binding.layouCustomer.visibility = View.VISIBLE
                    binding.layouNewCustomer.visibility = View.GONE
                    binding.layouLeadSource.visibility = View.GONE
                    // All fields editable
                    setFieldsEditable(false)
                }

                binding.rbNew.id -> {

                    customerType = "new"

                    selectedFirmId = ""
                    selectedFirmName = ""

                    binding.dropCity.setText("")

                    binding.layouNewCustomer.visibility = View.VISIBLE
                    binding.layouLeadSource.visibility = View.VISIBLE
                    binding.layouCustomer.visibility = View.GONE
                    setFieldsEditable(true)
                    clearForm()
                }
            }
        }

     //   viewModel.fetchLocation()
        selfieAdapter = ImageAdapter(
            selfieList,

            onDelete = { position ->

                selfieList.removeAt(position)

                selfieAdapter.notifyDataSetChanged()
            },


            onAdd = {
                binding.edtRemarks.clearFocus()
                selectedImageType = "selfie"
                checkCameraPermission()
              //  showImagePicker()
            },
            { position, mobile,type->
                showZoomDialog(this@TourDetailsActivity,mobile,type)
            },


            maxCount = 2,
        )

        bottomAdapter = ImageAdapter(
            bottomList,

            onDelete = { position ->

                bottomList.removeAt(position)

                bottomAdapter.notifyDataSetChanged()
            },

            onAdd = {
                selectedImageType = "bottom"
                checkCameraPermission()
              //  showImagePicker()
            },
            { position, mobile,type->
                showZoomDialog(this@TourDetailsActivity,mobile,type)
            },

            maxCount = 5
        )



        rvSelfie.layoutManager =
            LinearLayoutManager(
                this@TourDetailsActivity,
                RecyclerView.HORIZONTAL,
                false
            )

        rvBottom.layoutManager = GridLayoutManager(
            this@TourDetailsActivity,
            3
        )

        rvSelfie.adapter = selfieAdapter

        rvBottom.adapter = bottomAdapter







         val contactPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->

                if (granted) {

                    openContactList()

                } else {

                    Toast.makeText(
                        this@TourDetailsActivity,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }




     /*   binding.mobileNo1.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this@TourDetailsActivity,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                openContactList()

            } else {

                contactPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }*/




    }
    private fun showZoomDialog(
        context: Context,
        imageUrl: ImageItem,
        type: String
    ) {

        if (type=="image not found") {

            Toast.makeText(
                context,
                "Image not available",
                Toast.LENGTH_SHORT
            ).show()

            return
        }else{

            val dialog = Dialog(
                context,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            )

            dialog.setContentView(com.ssspvtltd.quick.R.layout.dialog_image_zoom)

            val photoView =
                dialog.findViewById<PhotoView>(com.ssspvtltd.quick.R.id.photoView)

            val btnClose =
                dialog.findViewById<ImageView>(com.ssspvtltd.quick.R.id.btnClose)

            try {

                if (type == "url") {

                    Glide.with(context)
                        .load(imageUrl.imageUrl)
                        .placeholder(com.ssspvtltd.quick.R.drawable.empty_photo)
                        .error(com.ssspvtltd.quick.R.drawable.empty_photo)
                        .into(photoView)

                } else {



                    Glide.with(context)
                        .load(imageUrl.bitmap)
                        .placeholder(com.ssspvtltd.quick.R.drawable.empty_photo)
                        .error(com.ssspvtltd.quick.R.drawable.empty_photo)
                        .into(photoView)
                }

            }
            catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    context,
                    "Failed to load image",
                    Toast.LENGTH_SHORT
                ).show()
            }

            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

    }
    private fun base64ToBitmap(base64String: String): Bitmap {

        val decodedBytes = Base64.decode(
            base64String,
            Base64.DEFAULT
        )

        return BitmapFactory.decodeByteArray(
            decodedBytes,
            0,
            decodedBytes.size
        )
    }

    private fun openContactList() {

        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )

        pickContact.launch(intent)
    }


    private var isFirmApiCalled = false

    private fun setupFirmDropdown() {

        binding.rbExisting.isChecked = true

        customerType = "existing"

        binding.layouCustomer.visibility = View.VISIBLE
        binding.layouNewCustomer.visibility = View.GONE
        setFieldsEditable(false)
        binding.dropCity.setOnClickListener {

            if (!binding.dropCity.isPopupShowing) {
                binding.dropCity.showDropDown()
            }else{
                // clickOnNickNameList()
            }

        }

        // First click -> API call + keyboard open
        binding.dropCity.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isFirmApiCalled) {

                    isFirmApiCalled = true

                    // API CALL
                    viewModel.getCustomerList()

                } else {

                    // Already loaded
                    if(customerType=="existing"){
                        binding.dropCity.showDropDown()
                    }else{
                        binding.dropCity.dismissDropDown()
                    }

                }
            }
        }

        // Clear State & Station


        binding.dropCity.doAfterTextChanged { editable ->

            val currentText = editable?.toString()?.trim() ?: ""

            if (currentText.isEmpty()) {

                selectedFirmId = ""
                selectedFirmName = ""

            } else if (currentText != selectedFirmName) {

                // User modified text manually
                selectedFirmId = ""
                selectedFirmName = ""
            }
        }
        binding.layouCustomer.setEndIconOnClickListener {

            // State
            binding.dropGrade.setText("", false)
            binding.dropCity.setText("", false)
            binding.dropState.setText("", false)
            binding.dropStation.setText("", false)
            binding.ownerName.setText("", false)
            binding.mobileNo1.setText("", false)
            binding.dropCategory.setText("", false)
            selectedStateId = ""
            selectedStateName = ""

            // Station

            selectedStationId = ""
            selectedStationName = ""

            selectedGradeId = ""
            selectedGradeName = ""

            selectedCategoryId = ""
            selectedCategoryName = ""

            isFirmApiCalled = true
        }

        // observe API list
        viewModel.frimList.observe(this) { list ->

            customerData = list


            val adapter = FirmAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                customerData
            )
         /*   val adapter = object : ArrayAdapter<AccountName>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(customerData)
            )
            {

                private var fullList: List<AccountName> = customerData

                override fun getFilter(): Filter {

                    return object : Filter() {

                        override fun performFiltering(
                            constraint: CharSequence?
                        ): FilterResults {

                            val query = constraint
                                ?.toString()
                                ?.lowercase()
                                ?.trim() ?: ""

                            val filtered = if (query.isEmpty()) {

                                fullList

                            } else {

                                fullList.filter {

                                    it.name.lowercase()
                                        .contains(query)
                                }
                            }

                            return FilterResults().apply {
                                values = filtered
                            }
                        }

                        override fun publishResults(
                            constraint: CharSequence?,
                            results: FilterResults?
                        ) {

                            clear()

                            addAll(
                                results?.values as List<AccountName>
                            )

                            notifyDataSetChanged()
                        }
                    }
                }

                // full text show
                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {

                    val view = super.getView(
                        position,
                        convertView,
                        parent
                    )

                    val item = getItem(position)

                    (view as TextView).text = item?.name

                    return view
                }
            }
*/
            binding.dropCity.setAdapter(adapter)

            binding.dropCity.threshold = 1

            // auto open dropdown after API
            binding.dropCity.showDropDown()
        }

        // item selected
        binding.dropCity.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as AccountName

            selectedFirmName=selectedItem.name
            selectedFirmId=selectedItem.id
            binding.dropCity.setText(
                selectedItem.name,
                false
            )


            viewModel.getAccountDetailsByID(selectedFirmId)

            viewModel.accountDetailsByID.observe(
                this
            )
            { list1 ->



                val data = list1
         /*       binding.dropCity.setText(
                    data?.firmName,
                    false
                )
                binding.mobileNo1.setText(data?.mobileNo)

                binding.mobileNo2.setText(data?.whatsappNo)
                binding.dropGrade.setText(data?.gradeName)
                selectedGradeId=data?.gradeId
                binding.dropStation.setText(data?.stationName)
                selectedStationId=data?.stationID
           //     binding.dropState.setText(data?.stateName)
                selectedStateId=data?.stateID
                binding.ownerName.setText(data?.ownerName)
                binding.mobileNo1.setText(data?.mobileNo)
                binding.mobileNo2.setText(data?.whatsappNo)
                binding.dropCategory.setText(data?.categoryName)
                selectedCategoryId=data?.categoryId
                binding.shopArea.setText(data?.shopAreaSqft)
                binding.workingBranchs.setText(data?.branchName)*/

                binding.apply {


                    dropCity.setText(data?.firmName ?: "", false)
                    selectedFirmId = data?.id
                    // selectedFirmId=data.fi

                    dropGrade.setText(data?.gradeName ?: "", false)
                    selectedGradeId = data?.gradeId

                    dropStation.setText(data?.stationName ?: "", false)
                    selectedStationId = data?.stationID


                    dropState.setText(data?.stateName ?: "", false)
                    selectedStateId = data?.stateID

                    ownerName.setText(data?.ownerName ?: "", false)

                    mobileNo1.setText(data?.mobileNo ?: "")

                    mobileNo2.setText(data?.whatsappNo ?: "")

                    dropCategory.setText(data?.categoryName ?: "", false)
                    selectedCategoryId = data?.categoryId

                    shopArea.setText(data?.shopAreaSqft ?: "")

                    workingBranchs.setText(data?.branchName ?: "")

               //     edtOldAgent.setText(data.oldAgentName ?: "")
                    /*  Glide.with(holder.itemView.context)
                      .load(item.imageUrl)
                      .into(holder.imgPhoto)*/

                /*    data.selfieImageURL1?.let {

                        selfieList.add(
                            ImageItem(imageUrl = it)
                        )
                    }

                    data.selfieImageURL2?.let {

                        selfieList.add(
                            ImageItem(imageUrl = it)
                        )
                    }



                    data.shopImageURL1?.let {

                        bottomList.add(
                            ImageItem(imageUrl = it)
                        )
                    }

                    data.shopImageURL2?.let {

                        bottomList.add(
                            ImageItem(imageUrl = it)
                        )
                    }

                    data.shopImageURL3?.let {

                        bottomList.add(
                            ImageItem(imageUrl = it)
                        )
                    }

                    data.shopImageURL4?.let {

                        bottomList.add(
                            ImageItem(imageUrl = it)
                        )
                    }

                    data.shopImageURL5?.let {

                        bottomList.add(
                            ImageItem(imageUrl = it)
                        )
                    }*/



                /*    selfieAdapter.notifyDataSetChanged()

                    bottomAdapter.notifyDataSetChanged()*/

                    edtYearlySale.setText(data?.yearlySale ?: "")

                   // edtRemarks.setText(data.remark ?: "")

               /*     txtLatLong.text =
                        "Long, Lat: ${data.longitude}, ${data.latitude}"*/


                    // SHOP CATEGORY SELECT

                    val selectedCategories =
                        data?.shopCategory
                            ?.split(",")
                            ?.map { it.trim() }
                            ?: emptyList()



                    ShopCategory.forEach { item ->

                        item.isSelected =
                            selectedCategories.any { selected ->

                                selected.equals(
                                    item.name,
                                    ignoreCase = true
                                )
                            }
                    }


                }

                adapter.notifyDataSetChanged()





            }

            // remove focus
            binding.dropCity.clearFocus()

            // border color
            binding.layouCustomer.setBoxStrokeColor(
                ContextCompat.getColor(this, R.color.black)
            )

            // hint color
            binding.layouCustomer.defaultHintTextColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.black)
                )

            // remove error
            binding.layouCustomer.error = null
            binding.layouCustomer.isErrorEnabled = false
        }
    }


    private fun setupGradeDropdown() {
        // click

        binding.dropGrade.setOnClickListener {
            if (!isGradeApiCalled) {
                isGradeApiCalled = true

                viewModel.getGradeList()

            } else {

                binding.dropGrade.showDropDown()
            }
        }

        // focus
        binding.dropGrade.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isGradeApiCalled) {

                    isGradeApiCalled = true

                    viewModel.getGradeList()

                } else {

                    binding.dropGrade.showDropDown()
                }
            }
        }

        // observe API
        viewModel.gradeList.observe(this) { list ->

            gradeData = list


            val adapter = GradeAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                gradeData
            )
          /*  val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                gradeData
            )*/

            binding.dropGrade.setAdapter(adapter)

            binding.dropGrade.showDropDown()
        }

        // item click
        binding.dropGrade.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as GradeItem

            selectedGradeName=selectedItem.name
            selectedGradeId=selectedItem.id
            binding.dropGrade.setText(
                selectedItem.name,
                false
            )


            // ✅ IMPORTANT FIX
            binding.dropGrade.clearFocus()
            binding.dropGrade.isCursorVisible = false

            // hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.dropCity.windowToken, 0)

            // remove error
            binding.layouGrade.error = null
        }

        binding.dropGrade.doAfterTextChanged { editable ->

            val currentText = editable?.toString()?.trim() ?: ""

            if (currentText.isEmpty()) {

                selectedGradeId = ""
                selectedGradeName = ""

            } else if (currentText != selectedGradeName) {

                // User modified text manually
                selectedGradeId = ""
                selectedGradeName = ""
            }
        }

        // clear icon click support
      /*  binding.dropGrade.setOnDismissListener {

            binding.dropGrade.clearFocus()
        }*/
    }

    private fun setupStationDropdown() {

        // Clear State & Station
        binding.layouState.setEndIconOnClickListener {

            // State
            binding.dropState.setText("", false)
            selectedStateId = ""
            selectedStateName = ""

            // Station
            binding.dropStation.setText("", false)
            selectedStationId = ""
            selectedStationName = ""

            stationData = emptyList()
            binding.dropStation.setAdapter(null)

            isStationApiCalled = false
        }

        // Station Click
        binding.dropStation.setOnClickListener {

            if (selectedStateId.isNullOrBlank()) {
                showToast("Select State First")
                return@setOnClickListener
            }

            if (!isStationApiCalled) {
                isStationApiCalled = true
                viewModel.getStationList(selectedStateId)
            } else {
                binding.dropStation.showDropDown()
            }
        }

        // API Response
        viewModel.stationList.observe(this) { list ->

            stationData = list ?: emptyList()

            val adapter = StationAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                stationData
            )

            binding.dropStation.setAdapter(adapter)

            if (stationData.isNotEmpty()) {
                binding.dropStation.showDropDown()
            }
        }

        // Item Selection
        binding.dropStation.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as StationItem

            selectedStationId = selectedItem.id
            selectedStationName = selectedItem.name

            binding.dropStation.setText(selectedItem.name, false)

            binding.dropStation.clearFocus()
            binding.dropStation.isCursorVisible = false

            // Hide Keyboard
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(
                binding.dropStation.windowToken,
                0
            )

            binding.layouStation.error = null
        }

        // Text Change
        binding.dropStation.doAfterTextChanged { editable ->

            val currentText = editable?.toString()?.trim() ?: ""

            if (currentText.isEmpty()) {

                selectedStationId = ""
                selectedStationName = ""

            } else if (currentText != selectedStationName) {

                // User modified text manually
                selectedStationId = ""
                selectedStationName = ""
            }
        }

        // Optional: Load stations on focus
        binding.dropStation.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) return@setOnFocusChangeListener

            if (selectedStateId.isNullOrBlank()) {
                showToast("Select State First")
                return@setOnFocusChangeListener
            }

            if (!isStationApiCalled) {

                isStationApiCalled = true
                viewModel.getStationList(selectedStateId)

            } else {

                binding.dropStation.showDropDown()
            }
        }
    }

    private fun setupLeadResourceDropdown() {

        // Clear State & Station
        binding.layouLeadSource.setEndIconOnClickListener {

            // State
            binding.dropLeadSource.setText("", false)
            selectedLeadResocureId = ""
            selectedLeadResocureName = ""

           /* // Station
            binding.dropStation.setText("", false)
            selectedStationId = ""
            selectedStationName = ""*/

         /*   stationData = emptyList()
            binding.dropStation.setAdapter(null)

            isStationApiCalled = false*/
        }

        // Station Click
        binding.dropLeadSource.setOnClickListener {
            binding.dropLeadSource.showDropDown()
        }
     /*   binding.dropLeadSource.setOnClickListener {


            if (!isLeadResourceApiCalled) {
                isLeadResourceApiCalled = true
                viewModel.getleadResourceList()
            } else {
                binding.dropLeadSource.showDropDown()
            }
        }*/

        // API Response
        viewModel.leadResourceList.observe(this) { list ->

            leadResourceData = list ?: emptyList()

            val adapter = LeadResourceAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                leadResourceData
            )

            binding.dropLeadSource.setAdapter(adapter)

            // Default selection
            val defaultItem = leadResourceData.firstOrNull {
                it.leadTypeName.equals("Marketer Visit", ignoreCase = true)
            }

            if (defaultItem != null) {

                selectedLeadResocureId = defaultItem.id
                selectedLeadResocureName = defaultItem.leadTypeName

                binding.dropLeadSource.setText(
                    defaultItem.leadTypeName,
                    false
                )
            }
        }

        // Item Selection
        binding.dropLeadSource.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as LeadResource

            selectedLeadResocureId = selectedItem.id
            selectedLeadResocureName = selectedItem.leadTypeName

            binding.dropLeadSource.setText(selectedItem.leadTypeName, false)

            binding.dropLeadSource.clearFocus()
            binding.dropLeadSource.isCursorVisible = false

            // Hide Keyboard
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(
                binding.dropLeadSource.windowToken,
                0
            )

            binding.layouLeadSource.error = null
        }

        // Text Change
        binding.dropLeadSource.doAfterTextChanged { editable ->

            val currentText = editable?.toString()?.trim() ?: ""

            if (currentText.isEmpty()) {

                selectedLeadResocureId = ""
                selectedLeadResocureName = ""

            } else if (currentText != selectedLeadResocureName) {

                // User modified text manually
                selectedLeadResocureId = ""
                selectedLeadResocureName = ""
            }
        }

    }

    private fun setupStateDropdown() {

        // click
        binding.dropState.setOnClickListener {

            if (!isStateApiCalled) {

                isStateApiCalled = true

                // First API Call
                viewModel.getStateList()

            } else {

                // Already loaded
                binding.dropState.showDropDown()
            }
        }

        binding.dropState.doAfterTextChanged {

            if (it.isNullOrEmpty()) {

                selectedStateId = ""
                selectedStateName = ""

                binding.dropStation.setText("", false)

                selectedStationId = ""
                selectedStationName = ""

                stationData = emptyList()

                binding.dropStation.setAdapter(null)

                isStationApiCalled = false
                // Show all states again
                binding.dropState.post {
                    if(it!!.isNotEmpty())
                    binding.dropState.showDropDown()
                }
            }
        }

        // focus
        binding.dropState.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isStateApiCalled) {

                    isStateApiCalled = true

                    // First API Call
                    viewModel.getStateList()

                } else {

                    // Already loaded
                    binding.dropState.showDropDown()
                }
            }
        }

        // observe API response
        viewModel.stateList.observe(this) { list ->

            stateData = list

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                stateData
            )

            binding.dropState.setAdapter(adapter)

            // auto open dropdown
            binding.dropState.showDropDown()
        }

        // item click
        binding.dropState.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as StateItem
            selectedStateId=selectedItem.id
            selectedStateName=selectedItem.name
            binding.dropState.setText(
                selectedItem.name,
                false
            )

            // ✅ IMPORTANT FIX
            binding.dropState.clearFocus()
            binding.dropState.isCursorVisible = false
            // Reset Station
            selectedStationId = ""
            selectedStationName = ""

            binding.dropStation.setText("", false)

            stationData = emptyList()

            isStationApiCalled = false

            // hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.dropCity.windowToken, 0)

            // remove error
            binding.layouState.error = null

        }

        // clear focus when dropdown dismiss
     /*   binding.dropState.setOnDismissListener {

            binding.dropState.clearFocus()
        }*/
    }

    private fun stateList(){

        // click
        binding.dropState.setOnClickListener {

            if (!isStateApiCalled) {

                isStateApiCalled = true

                // First API Call
                viewModel.getStateList()

            } else {

                // Already loaded
                binding.dropState.showDropDown()
            }
        }


        binding.dropState.doAfterTextChanged {

            if (it.isNullOrEmpty()) {

                selectedStateId = ""
                selectedStateName = ""

                binding.dropStation.setText("", false)

                selectedStationId = ""
                selectedStationName = ""

                stationData = emptyList()

                binding.dropStation.setAdapter(null)

                isStationApiCalled = false
                // Show all states again
                binding.dropState.post {
                    if(it!!.isNotEmpty())
                        binding.dropState.showDropDown()
                }
            }
        }


        // observe API response
        viewModel.stateList.observe(this) { list ->

            stateData = list


            val  nickNameAdapter = StateAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                stateData
            )



            binding.dropState.setAdapter(nickNameAdapter)

            // auto open dropdown
            binding.dropState.showDropDown()
        }


        binding.dropState.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as StateItem
            selectedStateId=selectedItem.id
            selectedStateName=selectedItem.name
            binding.dropState.setText(
                selectedItem.name,
                false
            )

            // ✅ IMPORTANT FIX
            binding.dropState.clearFocus()
            binding.dropState.isCursorVisible = false
            // Reset Station
            selectedStationId = ""
            selectedStationName = ""

            binding.dropStation.setText("", false)

            stationData = emptyList()

            isStationApiCalled = false

            // hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.dropCity.windowToken, 0)

            // remove error
            binding.layouState.error = null

        }




        /*  viewModel.getPurchaseParty(null,null, false)
          if (::purchasePartyAdapter.isInitialized) purchasePartyAdapter.updateType(false)
          isNickNameSelected = false*/

    }

    private fun setupCategoryeDropdown() {

        // click
        binding.dropCategory.setOnClickListener {

            if (!isCategoryApiCalled) {

                isCategoryApiCalled = true

                // First API Call
                viewModel.getStateList()

            } else {

                // Already loaded
                binding.dropCategory.showDropDown()
            }
        }

        // Text Change
        binding.dropCategory.doAfterTextChanged { editable ->

            val currentText = editable?.toString()?.trim() ?: ""

            if (currentText.isEmpty()) {

                selectedCategoryName = ""
                selectedCategoryId = ""

            } else if (currentText != selectedCategoryName) {

                // User modified text manually
                selectedCategoryName = ""
                selectedCategoryId = ""
            }
        }

        // focus
        binding.dropCategory.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isCategoryApiCalled) {

                    isCategoryApiCalled = true

                    // First API Call
                    viewModel.getCategoryList()

                } else {

                    // Already loaded
                    binding.dropCategory.showDropDown()
                }
            }
        }

        // observe API response
        viewModel.categoryList.observe(this) { list ->

            categoryData = list




         /*   val adapter = TourCategoryAdapter(
                this,
                R.layout.simple_dropdown_item_1line,
                categoryData
            )*/

            val  adapter = TourCategoryAdapter(
                true,
                this,
                com.ssspvtltd.quick.R.layout.item_saleparty,
                categoryData
            )

            binding.dropCategory.setAdapter(adapter)

            // auto open dropdown
            binding.dropCategory.showDropDown()
        }

        // item click
        binding.dropCategory.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as CategoryItem
            selectedCategoryId=selectedItem.id
            selectedCategoryName=selectedItem.name
            binding.dropCategory.setText(
                selectedItem.name,
                false
            )

            // ✅ IMPORTANT FIX
            binding.dropCategory.clearFocus()
            binding.dropCategory.isCursorVisible = false

            // hide keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.dropCategory.windowToken, 0)

            // remove error
            binding.layouCategory.error = null
        }

        // clear focus when dropdown dismiss
  /*      binding.dropCategory.setOnDismissListener {

            binding.dropState.clearFocus()
        }*/
    }

    private fun setupSubCategory() {

    viewModel.getShopCategoryList()


        // observe API response
        viewModel.shopcategoryList.observe(this) { list ->

            ShopCategory = list

            adapter = CategoryAdapter(list as MutableList<ShopCategoryItem>)

            binding.recyclerCategory.layoutManager =
                GridLayoutManager(this, 2)

            binding.recyclerCategory.adapter = adapter
        }

        // Select All Button
        binding.btnSelectAll.setOnClickListener {

            if (!isAllSelected) {

                adapter.selectAll()

                binding.btnSelectAll.text = "Unselect All"

                isAllSelected = true

            } else {

                adapter.unSelectAll()

                binding.btnSelectAll.text = "Select All"

                isAllSelected = false
            }
        }
    }

    private fun showImagePicker() {
        binding.edtRemarks.clearFocus()
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->

                if (which == 0) {
                    checkCameraPermission()
                } else {
                    openGallery()
                }

            }.show()
    }

    private fun openCamera() {
        cameraLauncher.launch(null)
    }
    private fun checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            openCamera()

        } else {

            cameraPermissionLauncher.launch(Manifest.permission.CAMERA) // ✅ correct
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    private fun bitmapToBase64(bitmap: ImageItem): String {

        val byteArrayOutputStream = ByteArrayOutputStream()

        bitmap.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val bytes = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


}