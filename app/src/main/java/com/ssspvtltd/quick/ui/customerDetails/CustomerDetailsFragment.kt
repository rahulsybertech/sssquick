package com.ssspvtltd.quick.ui.customerDetails

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentCustomerDetailsBinding
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.model.customer.NickName
import com.ssspvtltd.quick.model.customerdetails.PersonModel
import com.ssspvtltd.quick.model.editCustomer.EditCustomerData
import com.ssspvtltd.quick.ui.customerDetails.adapter.PersonAdapter
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.CustomerDetailsRequest
import com.ssspvtltd.quick.ui.customerDetails.modelRequest.Person
import com.ssspvtltd.quick.ui.customerDetails.viewmodel.CustomerDetailsViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CustomerDetailsFragment : BaseFragment<FragmentCustomerDetailsBinding, CustomerDetailsViewModel>() {
    var nickNameData: List<NickName> = emptyList()
    private lateinit var adapter: ArrayAdapter<AccountName>
    private var fullList = ArrayList<AccountName>()
    var customerData: List<AccountName> = emptyList()
    var editCustomerList: List<EditCustomerData> = emptyList()
  //  private val mAdapter by lazy { GoodsReturnAdaptoer() }
    private lateinit var personAdapter: PersonAdapter
    private val list = mutableListOf<PersonModel>()
    override val inflate: InflateF<FragmentCustomerDetailsBinding>
    get() = FragmentCustomerDetailsBinding::inflate
    private var isNickNameSelected = false
    private var isCustomerNameSelected = false
    private var isCustomerSelectWithNickName = false

    private var selectedPosition = -1
    private var selectedImageType = ""
    private var selectedNickNameId = ""
    private var selectedNickName = ""
    private var selectedCustomerName = ""
    private var selectedCustomerId = ""
    private var customerId: String? = null
    private var person: String? = ""
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->

            bitmap?.let {

                val base64 = bitmapToBase64(it)

                if (selectedPosition != -1) {

                    if (selectedImageType == "front") {
                        list[selectedPosition].aadharFrontBitmap = it
                        list[selectedPosition].aadharFrontBase64 = base64
                       // list[selectedPosition].backURL = ""
                    } else {
                        list[selectedPosition].aadharBackBitmap = it
                        list[selectedPosition].aadharBackBase64 = base64
                      //  list[selectedPosition].frontURL = ""
                    }

                    personAdapter.notifyItemChanged(selectedPosition)
                }
            }
        }


    val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            uri?.let {

                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    uri
                )

                val base64 = bitmapToBase64(bitmap)

                if (selectedPosition != -1) {

                    if (selectedImageType == "front") {
                        list[selectedPosition].aadharFrontBitmap = bitmap
                        list[selectedPosition].aadharFrontBase64 = base64
                   //     list[selectedPosition].frontURL = ""
                    } else {
                        list[selectedPosition].aadharBackBitmap = bitmap
                        list[selectedPosition].aadharBackBase64 = base64
                    //    list[selectedPosition].frontURL = ""
                    }

                    personAdapter.notifyItemChanged(selectedPosition)
                }
            }
        }


    private val pickContact =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data ?: return@registerForActivityResult

                val cursor = activity?.contentResolver?.query(
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

                            cleanNumber?.let { number ->

                                if (person.equals("customer")) {
                                    binding.etMobileNumber.setText(number)
                                } else {
                                    if (selectedPosition != -1) {
                                        list[selectedPosition].mobileNo = number
                                        personAdapter.notifyItemChanged(selectedPosition)
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }

    override fun initViewModel(): CustomerDetailsViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.apply {
            //  setTitle("Goods Return")
            setTitle("Customer Details")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
        initViews()
      //  setupNickNameDropdown()
        setupCustomerDropdown()
        registerObserver()
        viewModel.showProgressBar()
       // viewModel.factchCustomerNickNameList()
     //   viewModel.getCustomerList()


        binding.etMobileNumber.addTextChangedListener(object : TextWatcher {

            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (isUpdating) return

                var number = s.toString()

                // Remove spaces, +, -
                number = number.replace("\\s".toRegex(), "")
                    .replace("+", "")
                    .replace("-", "")

                // Remove country code 91
                if (number.startsWith("91") && number.length > 10) {
                    number = number.substring(2)
                }

                // Keep only last 10 digits
                if (number.length > 10) {
                    number = number.takeLast(10)
                }

                isUpdating = true
                binding.etMobileNumber.setText(number)
                binding.etMobileNumber.setSelection(number.length)
                isUpdating = false
            }
        })
        binding.tvAdd.setOnClickListener {

            if (!validateForm()) return@setOnClickListener

            val personList = list.map {
                if (!customerId.isNullOrEmpty()) {
                    Person(
                        id = if (it.id.isNullOrEmpty()) null else it.id,
                        personName = it.personName,
                        mobileNo   = it.mobileNo,
                        aadharFrontBase64 = it.aadharFrontBase64 ?: "",
                        aadharBackBase64 = it.aadharBackBase64 ?: "",
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                } else {
                    Person(
                        id = null,
                        personName = it.personName,
                        mobileNo   = it.mobileNo,
                        aadharFrontBase64 = it.aadharFrontBase64 ?: "",
                        aadharBackBase64 = it.aadharBackBase64 ?: "",
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                }

            }


            val request = CustomerDetailsRequest(
                id = if (customerId.isNullOrEmpty()) null else customerId,
                nickNameID = selectedNickNameId,
                accountID = selectedCustomerId,
                mobileNo = binding.etMobileNumber.text.toString(),
                remark = binding.etRemark.text.toString(),
                marketerID = null,
                nickName = selectedNickName,
                accountName = selectedCustomerName,
                date = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.getDefault()
                ).format(Date()),
                persons = personList
            )

            Log.e("request",request.toString())
         /*   val gson = Gson()
            val json = gson.toJson(request)

            Log.e("request_json", json)*/
            viewModel.addCustomerDetailReq(request)
        }

    }

    private var isCustomerApiCalled = false

    private fun setupCustomerDropdown() {

        // First click -> API + keyboard + dropdown
        binding.dropCity.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                if (!isCustomerApiCalled) {


                    // API CALL
                    viewModel.getCustomerList()

                } else {

                    // already loaded
                    binding.dropCity.showDropDown()
                }
            }
        }


        adapter = object : ArrayAdapter<AccountName>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ArrayList()
        ) {

            override fun getFilter(): Filter {

                return object : Filter() {

                    override fun performFiltering(constraint: CharSequence?): FilterResults {

                        val query = constraint?.toString()?.trim()?.lowercase().orEmpty()

                        val filteredList = if (query.isEmpty()) {
                            fullList
                        } else {
                            ArrayList(
                                fullList.filter {
                                    it.name.lowercase().contains(query)
                                }
                            )
                        }

                        return FilterResults().apply {
                            values = filteredList
                        }
                    }

                    override fun publishResults(
                        constraint: CharSequence?,
                        results: FilterResults?
                    ) {

                        clear()

                        val resultList =
                            results?.values as? ArrayList<AccountName> ?: arrayListOf()

                        addAll(resultList)

                        notifyDataSetChanged()
                    }
                }
            }

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getView(position, convertView, parent)

                (view as TextView).text = getItem(position)?.name

                return view
            }
        }

        binding.dropCity.setAdapter(adapter)
        binding.dropCity.threshold = 1
        // observe customer list
        viewModel.customerList.observe(viewLifecycleOwner) { list ->

            customerData = list ?: emptyList()

            isCustomerApiCalled = customerData.isNotEmpty()

            fullList.clear()
            fullList.addAll(customerData)

            adapter.clear()
            adapter.addAll(customerData)
            adapter.notifyDataSetChanged()

            // delay avoids UI timing issue
            binding.dropCity.postDelayed({

                if (isAdded && customerData.isNotEmpty()) {
                    binding.dropCity.showDropDown()
                }

            }, 200)
        }

        // item selected
        binding.dropCity.setOnItemClickListener { parent, _, position, _ ->

            isCustomerSelectWithNickName = false

            val selectedCustomer =
                parent.getItemAtPosition(position) as? AccountName
                    ?: return@setOnItemClickListener

            selectedCustomerId = selectedCustomer.id
            selectedCustomerName = selectedCustomer.name

            selectedNickNameId = selectedCustomer.nickNameID
            selectedNickName = selectedCustomer.nickName ?: ""

            isCustomerNameSelected = true
            isNickNameSelected = true

            val jsonObject = JsonObject().apply {

                addProperty("id", selectedCustomerId)

                addProperty(
                    "nickNameId",
                    selectedNickNameId
                )
            }

            viewModel.fatchAccountDetailsForID(jsonObject)

            viewModel.accountDetailsForID.observe(
                viewLifecycleOwner
            ) { list1 ->

                if (list1.isNullOrEmpty()) {

                    personAdapter.updateList(
                        listOf(PersonModel())
                    )

                    return@observe
                }

                val data = list1.first()

                binding.dropNickName.setText(
                    data.nickName,
                    false
                )

                isCustomerNameSelected = true
                isNickNameSelected = true

             //   selectedNickNameId = data.nickNameID
            //    selectedCustomerId = data.accountID

                binding.dropCity.setText(
                    data.accountName,
                    false
                )

                binding.etMobileNumber.setText(
                    data.mobileNo
                )

                binding.etRemark.setText(
                    data.remark
                )

                val persons = data.persons?.map {

                    PersonModel(
                        id = it.id ?: "",
                        personName = it.personName ?: "",
                        mobileNo = it.mobileNo ?: "",
                        frontURL = it.frontURL ?: "",
                        backURL = it.backURL ?: ""
                    )

                } ?: emptyList()

                if (persons.isEmpty()) {

                    personAdapter.updateList(
                        listOf(PersonModel())
                    )

                } else {

                    personAdapter.updateList(persons)
                }
            }

            // lock value
            binding.dropCity.setText(
                selectedCustomer.toString(),
                false
            )

            binding.dropNickNameByCustomerId.setText(
                selectedCustomer.nickName,
                false
            )

            binding.dropNickName.setText(
                selectedCustomer.nickName,
                false
            )

            // remove focus
            binding.dropCity.clearFocus()
        }

        // typing listener
        binding.dropCity.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }

    private fun registerObserver() {

        viewModel.customerListByNickNameId.observe(viewLifecycleOwner) { list ->
            val customerList = list.map { it.name }
            customerData=list
            val customerAdapterByNickNameId = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                customerList
            )
            binding.dropCutomerByNickNameId.setAdapter(customerAdapterByNickNameId)
        }

        binding.dropCutomerByNickNameId.setOnItemClickListener { _, _, position, _ ->

            val selectedCustomer = customerData[position]
            selectedCustomerId = selectedCustomer.id
            selectedCustomerName=selectedCustomer.name
            isCustomerNameSelected=true
            val jsonObject = JsonObject().apply {
                /*   addProperty("id", customerId)*/
                addProperty("nickNameId", selectedNickNameId)
                addProperty("customerId", selectedCustomerId)
            }

        }

        binding.dropCutomerByNickNameId.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    isCustomerNameSelected=false
                    selectedCustomerName=""
                    binding.dropNickNameByCustomerId.setText("", false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        viewModel.addCustomerResult.observe(viewLifecycleOwner) { data ->

            if (data.isSuccess) {
                showToast(data.applicationMessage)
                val isEdit = !customerId.isNullOrEmpty()

                if (isEdit) {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                } else {
                    activity?.onBackPressedDispatcher?.onBackPressed()
                }
            } else {
                showToast(data.applicationMessage)

            }
        }
    }

    private fun validateForm(): Boolean {

        // 1. Nick Name validation
    /*    if (!isNickNameSelected) {
            binding.dropNickName.error = "Select Nick Name"
            binding.dropNickName.requestFocus()
            return false
        }*/

        // 2. Customer validation

        if (isCustomerSelectWithNickName) {

            // 👉 When using NickName dropdown
            if (!isCustomerNameSelected) {
               // binding.layouCustomerBYNickNameId.error = "Select Customer"
                binding.dropCutomerByNickNameId.requestFocus()
                return false
            } else {
                binding.layouCustomerBYNickNameId.error = null
                binding.layouCustomerBYNickNameId.isErrorEnabled = false
            }

        } else {

            // 👉 Normal customer dropdown
            if (!isCustomerNameSelected) {
               // binding.layouCustomer.error = "Select Customer"
                binding.dropCity.requestFocus()
                return false
            } else {
                binding.layouCustomer.error = null
                binding.layouCustomer.isErrorEnabled = false
            }
        }


        // 3. Mobile validation


    /*    if (mobile.isEmpty()) {
            binding.etMobileNumber.error = "Enter Mobile Number"
            binding.etMobileNumber.requestFocus()
            return false
        }*/
     //   val mobile = binding.etMobileNumber.text.toString().trim()

// ✅ Only validate if NOT empty
      /*  if (mobile.isNotEmpty() && !mobile.matches(Regex("^[6-9][0-9]{9}$"))) {
            binding.etMobileNumber.error = "Enter valid mobile number"
            binding.etMobileNumber.requestFocus()
            return false
        }*/

        val mobile = binding.etMobileNumber.text.toString().trim()

        if (mobile.isNotEmpty()) {

            if (!mobile.matches(Regex("^[6-9][0-9]{9}$"))) {

                binding.layoutPersonMobileNum.error =
                    "Enter valid 10 digit mobile number"

                return false

            } else {

                binding.layoutPersonMobileNum.error = null

            }

        } else {

            // Empty is allowed
            binding.layoutPersonMobileNum.error = null

        }


        // 5. Person list validation
        list.forEachIndexed { index, person ->

            if (person.personName.isEmpty()) {

                showToast("Enter person name")

               binding. recyclerView.scrollToPosition(index)

                personAdapter.requestNameFocus(index)

                return false
            }

            val mobile = person.mobileNo.trim()

            if (mobile.isEmpty()) {

                showToast("Please Enter Person Mobile Number")

                binding.recyclerView.scrollToPosition(index)

                personAdapter.requestMobileFocus(index)

                return false
            }

            if (!mobile.matches(Regex("^[6-9]\\d{9}$"))) {

                showToast("Please Enter Person Mobile Number")

                binding.recyclerView.scrollToPosition(index)

                personAdapter.requestMobileFocus(index)

                return false
            }

            if (
                person.aadharFrontBase64.isNullOrEmpty() &&
                person.frontURL.isNullOrEmpty() &&
                person.aadharBackBase64.isNullOrEmpty() &&
                person.backURL.isNullOrEmpty()
            ) {
                binding.recyclerView.scrollToPosition(index)

                // Hide keyboard + clear focus
                requireActivity().currentFocus?.clearFocus()
                val imm = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager

                imm.hideSoftInputFromWindow(
                    binding.root.windowToken,
                    0
                )
                showToast("Upload Aadhar photo at least one side")

               binding. recyclerView.scrollToPosition(index)

                return false
            }
        }

        return true
    }

    private fun initViews() = with(binding) {

        val contactPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->

                if (granted) {

                    openContactList()

                } else {

                    Toast.makeText(
                        activity,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        customerId = arguments?.getString("id")
        list.clear()

        // ✅ ALWAYS initialize adapter first
        personAdapter = PersonAdapter(
            list,
            onAddClick = { position ->
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
                if (list.size < 20) {
                    list.add(position + 1, PersonModel())
                    personAdapter.notifyItemInserted(position + 1)
                    personAdapter.notifyItemChanged(position)
                } else {
                    Toast.makeText(context, "Maximum 20 person allowed", Toast.LENGTH_SHORT).show()
                }
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
            },
            onRemoveClick = { position ->
                if (list.size > 1) {

                    list.removeAt(position)

                    personAdapter.notifyItemRemoved(position)

                    // ✅ IMPORTANT: refresh last item
                    personAdapter.notifyItemChanged(list.size - 1)
                }
            },
            onAadharFrontClick = { position ->
                selectedPosition = position
                selectedImageType = "front"
                showImagePicker()
            },
            onAadharBackClick = { position ->
                selectedPosition = position
                selectedImageType = "back"
                showImagePicker()
            },
            { position, mobile->
                selectedPosition = position
                person="person"
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    openContactList()

                } else {

                    contactPermissionLauncher.launch(
                        Manifest.permission.READ_CONTACTS
                    )
                }
            },
            { position, mobile->
              showZoomDialog(requireContext(),mobile)
            },
            onRequestNextFocus = { nextPosition ->
                recyclerView.scrollToPosition(nextPosition)

                recyclerView.postDelayed({
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(nextPosition)
                    val editText = viewHolder?.itemView?.findViewById<EditText>(R.id.etPersonName)
                    editText?.requestFocus()
                }, 100)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = personAdapter

        // ✅ NOW handle data
        if (!customerId.isNullOrEmpty()) {

            toolbar.setTitle("Edit Customer Details")
            tvAdd.setText("Update")

            val jsonObject = JsonObject().apply {
                addProperty("id", customerId)
           /*     addProperty("nickNameId", "")
                addProperty("customerId", "")*/
            }
            viewModel.fatchAccountDetailsForID(jsonObject)

            viewModel.accountDetailsForID.observe(viewLifecycleOwner) { list1 ->

                if (list1.isNullOrEmpty()) {
                    // show one empty item
                    personAdapter.updateList(listOf(PersonModel()))
                    return@observe
                }

                val data = list1.first()

                binding.dropNickName.setText(data.nickName, false)
                isCustomerNameSelected = true
                isNickNameSelected = true
                selectedNickNameId = data.nickNameID
                selectedCustomerId = data.accountID
                customerId = data.id

                binding.dropCity.setText(data.accountName, false)
                binding.etMobileNumber.setText(data.mobileNo)
                binding.etRemark.setText(data.remark)

                val persons = data.persons?.map {

                    PersonModel(
                        id = it.id,
                        personName = it.personName ?: "",
                        mobileNo = it.mobileNo ?: "",
                        frontURL = it.frontURL ?: "",
                        backURL = it.backURL ?: ""
                    )

                } ?: emptyList()

                if (persons.isEmpty()) {
                    personAdapter.updateList(listOf(PersonModel()))
                } else {
                    personAdapter.updateList(persons)
                }
            }

        } else {
            toolbar.setTitle("Customer Details")
            tvAdd.setText("Save")
            list.add(PersonModel())
            personAdapter.notifyDataSetChanged()   // ✅ refresh
        }




        binding.imgPhoneBook.setOnClickListener {

            person="customer"
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                openContactList()

            } else {

                contactPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }
    }
    private fun openContactList() {

        val intent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )

        pickContact.launch(intent)
    }

    private fun showImagePicker() {

        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(requireContext())
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
                requireContext(),
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
    
    
    private fun showZoomDialog(context: Context, imageUrl: String) {

        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        dialog.setContentView(R.layout.dialog_image_zoom)

        val photoView = dialog.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photoView)

        val btnClose = dialog.findViewById<ImageView>(R.id.btnClose)

        Glide.with(context)
            .load(imageUrl)
            .into(photoView)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}