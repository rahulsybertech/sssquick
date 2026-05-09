package com.ssspvtltd.quick.ui.tour

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ssspvtltd.quick.base.BaseActivity
import com.ssspvtltd.quick.base.InflateA
import com.ssspvtltd.quick.databinding.ActivityTourDetailsBinding
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.ui.order.add.adapter.AddImageAdapter
import com.ssspvtltd.quick.ui.tour.adapter.CategoryAdapter
import com.ssspvtltd.quick.ui.tour.adapter.ImageAdapter
import com.ssspvtltd.quick.ui.tour.model.CategoryItem
import com.ssspvtltd.quick.ui.tour.model.GradeItem
import com.ssspvtltd.quick.ui.tour.model.LeadRequest
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
    private var isStateApiCalled = false
    private var isCategoryApiCalled = false
    var customerData: List<AccountName> = emptyList()
    var gradeData: List<GradeItem> = emptyList()
    var stationData: List<StationItem> = emptyList()
    var stateData: List<StateItem> = emptyList()
    var categoryData: List<CategoryItem> = emptyList()
    private var isNextScreen = false
    private lateinit var adapter: CategoryAdapter
    private var isAllSelected = false
    private var selectedImageType = ""
    private val selfieList = mutableListOf<Bitmap>()
    private val bottomList = mutableListOf<Bitmap>()
    private lateinit var selfieAdapter: ImageAdapter
    private lateinit var bottomAdapter: ImageAdapter
    private var selectedFirmName: String? = null
    private var selectedFirmId: String? = null
    private var selectedGradeName: String? = null
    private var selectedGradeId: String? = null
    private var selectedStateName: String? = null
    private var selectedStateId: String? = null
    private var selectedStationName: String? = null
    private var selectedStationId: String? = null
    private var ownerName: String? = null
    private var mobileNumber: String? = null
    private var whatppMobileNumber: String? = null
    private var selectedCategoryName: String? = null
    private var selectedCategoryId: String? = null


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

                        selfieList.add(it)

                        selfieAdapter.notifyDataSetChanged()
                    }

                } else {

                    if (bottomList.size < 5) {

                        bottomList.add(it)

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

                        selfieList.add(bitmap)

                        selfieAdapter.notifyDataSetChanged()
                    }

                } else {

                    if (bottomList.size < 5) {

                        bottomList.add(bitmap)

                        bottomAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    override val inflate: InflateA<ActivityTourDetailsBinding> get() = ActivityTourDetailsBinding::inflate
    override fun initViewModel(): TourDetailsViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
     //   registerObserver()
      //  registerListener()
        setupFirmDropdown()
        setupGradeDropdown()
        setupStationDropdown()
        setupStateDropdown()
        setupCategoryeDropdown()
        setupSubCategory()
        binding.mobileNo2.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {

                binding.nestedScrollView.post {
                    binding.nestedScrollView.smoothScrollTo(
                        0,
                        view.top
                    )
                }
            }
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

                isNextScreen = true

                binding.btnNext.text = "Save"

                binding.btnBack.visibility = View.VISIBLE

                binding.mainLayout.visibility = View.GONE

                binding.layoutStepTwo.visibility = View.VISIBLE

            } else {

                // SECOND SCREEN → SAVE

                saveData()
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

        binding.btnBack.setOnClickListener {

            isNextScreen = false

            binding.btnNext.text = "Next"

            binding.btnBack.visibility = View.GONE

            binding.mainLayout.visibility = View.VISIBLE

            binding.layoutStepTwo.visibility = View.GONE
        }






    }

    private fun saveData() {
        val selectedCategoryList = adapter.getSelectedCategories()
        val request = LeadRequest(

            leadNo = 1,

            date = getCurrentDateTime(),

            ownerName = ownerName ?: "",

            firmName = selectedFirmName ?: "",

            mobileNo = mobileNumber ?: "",

            whatsappNo = whatppMobileNumber ?: "",

            stateId = selectedStateId ?: "",

            stateName = selectedStateName ?: "",

            stationId = selectedStationId ?: "",

            stationName = selectedStationName ?: "",

            categoryId = selectedCategoryId ?: "",

            categoryName = selectedCategoryName ?: "",

            gradeID = selectedGradeId ?: "",

            gradeName = selectedGradeName ?: "",

            oldAgentName = binding.edtOldAgent.text.toString(),

            yearlySale = binding.edtYearlySale.text.toString(),

            remark = binding.edtRemarks.text.toString(),
            accountID = null,
            companyId = null,

            latitude = "latitude" ?: "",

            longitude = "longitude" ?: "",

            selfieImage1 =
                if (selfieList.size > 0)
                    bitmapToBase64(selfieList[0])
                else "",

            selfieImage2 =
                if (selfieList.size > 1)
                    bitmapToBase64(selfieList[1])
                else "",

            shopImage1 =
                if (bottomList.size > 0)
                    bitmapToBase64(bottomList[0])
                else "",

            shopImage2 =
                if (bottomList.size > 1)
                    bitmapToBase64(bottomList[1])
                else "",

            shopImage3 =
                if (bottomList.size > 2)
                    bitmapToBase64(bottomList[2])
                else "",

            shopImage4 =
                if (bottomList.size > 3)
                    bitmapToBase64(bottomList[3])
                else "",

            shopImage5 =
                if (bottomList.size > 4)
                    bitmapToBase64(bottomList[4])
                else "",

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

        selfieAdapter = ImageAdapter(
            selfieList,

            onDelete = { position ->

                selfieList.removeAt(position)

                selfieAdapter.notifyDataSetChanged()
            },

            onAdd = {

                selectedImageType = "selfie"

                showImagePicker()
            },

            maxCount = 2
        )

        bottomAdapter = ImageAdapter(
            bottomList,

            onDelete = { position ->

                bottomList.removeAt(position)

                bottomAdapter.notifyDataSetChanged()
            },

            onAdd = {

                selectedImageType = "bottom"

                showImagePicker()
            },

            maxCount = 5
        )

        rvSelfie.layoutManager =
            LinearLayoutManager(
                this@TourDetailsActivity,
                RecyclerView.HORIZONTAL,
                false
            )

        rvBottom.layoutManager =
            LinearLayoutManager(
                this@TourDetailsActivity,
                RecyclerView.HORIZONTAL,
                false
            )

        rvSelfie.adapter = selfieAdapter

        rvBottom.adapter = bottomAdapter




    }
    private var isFirmApiCalled = false

    private fun setupFirmDropdown() {

        // First click -> API call + keyboard open
        binding.dropCity.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isFirmApiCalled) {

                    isFirmApiCalled = true

                    // API CALL
                    viewModel.getCustomerList()

                } else {

                    // Already loaded
                    binding.dropCity.showDropDown()
                }
            }
        }

        // observe API list
        viewModel.frimList.observe(this) { list ->

            customerData = list

            val adapter = object : ArrayAdapter<AccountName>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(customerData)
            ) {

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

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                gradeData
            )

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
        }

        // clear icon click support
        binding.dropGrade.setOnDismissListener {

            binding.dropGrade.clearFocus()
        }
    }

    private fun setupStationDropdown() {

        // click
        binding.dropStation.setOnClickListener {

            if (!isStationApiCalled) {

                isStationApiCalled = true

                // First API Call
                viewModel.getStationList()

            } else {

                // Already loaded
                binding.dropStation.showDropDown()
            }
        }

        // focus
        binding.dropStation.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isStationApiCalled) {

                    isStationApiCalled = true

                    // First API Call
                    viewModel.getStationList()

                } else {

                    // Already loaded
                    binding.dropStation.showDropDown()
                }
            }
        }

        // observe API response
        viewModel.stationList.observe(this) { list ->

            stationData = list

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                stationData
            )

            binding.dropStation.setAdapter(adapter)

            // auto open dropdown
            binding.dropStation.showDropDown()
        }

        // item click
        binding.dropStation.setOnItemClickListener { parent, _, position, _ ->

            val selectedItem =
                parent.getItemAtPosition(position) as StationItem

            selectedStationId=selectedItem.id
            selectedStationName=selectedItem.name
            binding.dropStation.setText(
                selectedItem.name,
                false
            )
        }

        // dismiss support
        binding.dropStation.setOnDismissListener {

            binding.dropStation.clearFocus()
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
        }

        // clear focus when dropdown dismiss
        binding.dropState.setOnDismissListener {

            binding.dropState.clearFocus()
        }
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

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
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
        }

        // clear focus when dropdown dismiss
        binding.dropCategory.setOnDismissListener {

            binding.dropState.clearFocus()
        }
    }

    private fun setupSubCategory() {

    viewModel.getShopCategoryList()

        // observe API response
        viewModel.shopcategoryList.observe(this) { list ->

           // categoryData = list

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
    private fun bitmapToBase64(bitmap: Bitmap): String {

        val byteArrayOutputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val bytes = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}