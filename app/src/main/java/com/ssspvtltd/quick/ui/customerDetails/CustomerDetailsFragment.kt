package com.ssspvtltd.quick.ui.customerDetails

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Filter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentCustomerDetailsBinding
import com.ssspvtltd.quick.model.customer.AccountName
import com.ssspvtltd.quick.model.customer.NickName
import com.ssspvtltd.quick.model.customerdetails.CustomerList
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
                        list[selectedPosition].frontURL = ""
                    } else {
                        list[selectedPosition].aadharBackBitmap = bitmap
                        list[selectedPosition].aadharBackBase64 = base64
                        list[selectedPosition].frontURL = ""
                    }

                    personAdapter.notifyItemChanged(selectedPosition)
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
        viewModel.factchCustomerNickNameList()
        viewModel.getCustomerList()

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
                        frontURL = "",
                        backURL = ""
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
            val gson = Gson()
            val json = gson.toJson(request)

            Log.e("request_json", json)
            viewModel.addCustomerDetailReq(request)
        }

    }

    private fun setupNickNameDropdown() {

        // 👉 show dropdown on click
        binding.dropNickName.setOnClickListener {
            binding.dropNickName.showDropDown()
        }

        // 👉 show dropdown on focus
        binding.dropNickName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.dropNickName.showDropDown()
        }

        // 👉 observe API list
        viewModel.customerNickNameList.observe(viewLifecycleOwner) { list ->
            nickNameData = list

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nickNameData
            )

            binding.dropNickName.setAdapter(adapter)
        }

        // 👉 item click (FIXED)
        binding.dropNickName.setOnItemClickListener { parent, _, position, _ ->
            isCustomerSelectWithNickName=true
            val selectedNick = parent.getItemAtPosition(position) as? NickName
                ?: return@setOnItemClickListener

            selectedNickNameId = selectedNick.id

            // ✅ FIX: use same value as UI
            selectedNickName = selectedNick.nickName ?: selectedNick.name

            isNickNameSelected = true

            // ✅ lock correct value in UI
            binding.dropNickName.setText(selectedNick.toString(), false)

            binding.dropNickName.error = null
            binding.dropNickName.clearFocus()

            // reset customer
            selectedCustomerId = ""
            selectedCustomerName = ""
            binding.dropCutomerByNickNameId.setText("", false)

            binding.layouCustomerBYNickNameId.visibility = View.VISIBLE
            binding.layouCustomer.visibility = View.GONE

            viewModel.factchCustomerListByNickNameId(selectedNick.id)
            val jsonObject = JsonObject().apply {
                /*   addProperty("id", customerId)*/
                addProperty("nickNameId", selectedNickNameId)
       //         addProperty("id", selectedCustomerId)
            }
            viewModel.fatchAccountDetailsForID(jsonObject)
            viewModel.accountDetailsForID.observe(viewLifecycleOwner) { list1 ->

                if (list1.isNullOrEmpty()) {
                    // Show one empty item
                    personAdapter.updateList(listOf(PersonModel()))

                    return@observe
                }

                val data = list1.first()

           //     binding.dropNickName.setText(data.nickName, false)
                isCustomerNameSelected = true
                isNickNameSelected = true
                /*      selectedNickNameId = data.nickNameID
                      selectedCustomerId = data.accountID*/
                //   customerId = data.id

            //    binding.dropCity.setText(data.accountName, false)
             //   binding.etMobileNumber.setText(data.mobileNo)

                val persons = data.persons?.map {
                    PersonModel(
                        id = it.id,
                        personName = it.personName,
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                } ?: emptyList()

                if (persons.isEmpty()) {
                    personAdapter.updateList(listOf(PersonModel())) // 👈 show empty item
                } else {
                    personAdapter.updateList(persons)
                }
            }
        }

        // 👉 handle manual typing (FIXED)
        binding.dropNickName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val text = s?.toString()?.trim()

                val match = nickNameData.find {
                    (it.nickName ?: it.name).equals(text, ignoreCase = true)
                }

                if (match == null) {
                    // ❌ user typed manually → reset everything
                    selectedNickNameId = ""
                    selectedNickName = ""
                    isNickNameSelected = false

                    binding.layouCustomerBYNickNameId.visibility = View.GONE
                    binding.layouCustomer.visibility = View.VISIBLE
                }
                if(text!!.isEmpty()){
                  binding.layoutNickNameByCustomerId.visibility= View.GONE
                  binding.layoutNickName.visibility= View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.dropNickNameByCustomerId.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val text = s?.toString()?.trim()


                if(text!!.isEmpty()){
                    binding.layoutNickNameByCustomerId.visibility= View.GONE
                    binding.layoutNickName.visibility= View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setupCustomerDropdown() {

        // 👉 show dropdown on click
        binding.dropCity.setOnClickListener {
            binding.dropCity.showDropDown()
        }

        // 👉 show dropdown on focus
        binding.dropCity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.dropCity.showDropDown()
        }

        // 👉 observe API list
        viewModel.customerList.observe(viewLifecycleOwner) { list ->

            customerData = list

            val adapter = object : ArrayAdapter<AccountName>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(customerData)
            ) {

                private var fullList: List<AccountName> = customerData

                override fun getFilter(): Filter {
                    return object : Filter() {

                        override fun performFiltering(constraint: CharSequence?): FilterResults {
                            val query = constraint?.toString()?.lowercase()?.trim() ?: ""

                            val filtered = if (query.isEmpty()) {
                                fullList
                            } else {
                                fullList.filter {
                                    it.name.lowercase().contains(query)   // ✅ search anywhere
                                }
                            }

                            return FilterResults().apply {
                                values = filtered
                            }
                        }

                        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                            clear()
                            addAll(results?.values as List<AccountName>)
                            notifyDataSetChanged()
                        }
                    }
                }

                // 👉 IMPORTANT: Show full text
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val item = getItem(position)
                    (view as TextView).text = item?.name   // ✅ FULL DISPLAY
                    return view
                }
            }

            binding.dropCity.setAdapter(adapter)
            binding.dropCity.threshold = 1
        }

        // 👉 item click (FIXED)
        binding.dropCity.setOnItemClickListener { parent, _, position, _ ->
            isCustomerSelectWithNickName=false
            val selectedCustomer = parent.getItemAtPosition(position) as? AccountName
                ?: return@setOnItemClickListener

            selectedCustomerId = selectedCustomer.id
            selectedCustomerName = selectedCustomer.name

            selectedNickNameId = selectedCustomer.nickNameID
            selectedNickName = selectedCustomer.nickName ?: ""

            isCustomerNameSelected = true
            isNickNameSelected = true
            val jsonObject = JsonObject().apply {
                addProperty("id", customerId)
                     addProperty("nickNameId", selectedNickNameId)
                     addProperty("id", selectedCustomerId)
            }
            viewModel.fatchAccountDetailsForID(jsonObject)
            viewModel.accountDetailsForID.observe(viewLifecycleOwner) { list1 ->

                if (list1.isNullOrEmpty()) {
                    // Show one empty item
                    personAdapter.updateList(listOf(PersonModel()))

                    return@observe
                }

                val data = list1.first()

                binding.dropNickName.setText(data.nickName, false)
                isCustomerNameSelected = true
                isNickNameSelected = true
                selectedNickNameId = data.nickNameID
                selectedCustomerId = data.accountID
             //   customerId = data.id

                binding.dropCity.setText(data.accountName, false)
                binding.etMobileNumber.setText(data.mobileNo)
                binding.etRemark.setText(data.remark)

                val persons = data.persons?.map {
                    PersonModel(
                        id = it.id,
                        personName = it.personName,
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                } ?: emptyList()

                if (persons.isEmpty()) {
                    personAdapter.updateList(listOf(PersonModel())) // 👈 show empty item
                } else {
                    personAdapter.updateList(persons)
                }
            }

            // ✅ lock correct value
            binding.dropCity.setText(selectedCustomer.toString(), false)

            // UI updates
         /*   binding.layoutNickNameByCustomerId.visibility = View.VISIBLE
            binding.layoutNickName.visibility = View.INVISIBLE
*/
            binding.dropNickNameByCustomerId.setText(selectedCustomer.nickName, false)
            binding.dropNickName.setText(selectedCustomer.nickName, false)
        }

        // 👉 handle manual typing (VERY IMPORTANT)
        binding.dropCity.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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

        /*    viewModel.fatchAccountDetailsForID(jsonObject)

            viewModel.accountDetailsForID.observe(viewLifecycleOwner) { list1 ->

                if (list1.isNullOrEmpty()) {

                    // Show one empty item
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

                val persons = data.persons?.map {
                    PersonModel(
                        id = it.id,
                        personName = it.personName,
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                } ?: emptyList()

                if (persons.isEmpty()) {
                    personAdapter.updateList(listOf(PersonModel())) // 👈 show empty item
                } else {
                    personAdapter.updateList(persons)
                }
            }*/
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
        val mobile = binding.etMobileNumber.text.toString().trim()

// ✅ Only validate if NOT empty
        if (mobile.isNotEmpty() && !mobile.matches(Regex("^[6-9][0-9]{9}$"))) {
            binding.etMobileNumber.error = "Enter valid Indian mobile number"
            binding.etMobileNumber.requestFocus()
            return false
        }

// ✅ Clear error if valid or empty
        binding.etMobileNumber.error = null

        // 4. Dropdown selection validation (important)
        /* if (!isNickNameSelected) {
             showToast("Please select Nick Name from list")
             return false
         }

         if (selectedCustomerId.isEmpty()) {
             showToast("Please select Customer from list")
             return false
         }*/

        // 5. Person list validation
        list.forEachIndexed { index, person ->

            if (person.personName.isEmpty()) {
              //  showToast("Enter person name at position ${index + 1}")
                showToast("Enter person name")
                return false
            }
            if (person.mobileNo.isEmpty()) {
                //  showToast("Enter person name at position ${index + 1}")
                showToast("Enter valid Indian mobile number")
                return false
            }


            if (person.aadharFrontBase64.isNullOrEmpty()&&person.frontURL.isNullOrEmpty()&&person.aadharBackBase64.isNullOrEmpty()&&person.backURL.isNullOrEmpty()) {
            //    showToast("Upload Aadhar photo at least one side  ${index + 1}")
                showToast("Upload Aadhar photo at least one side")
                return false
            }

        }

        return true
    }

    private fun initViews() = with(binding) {

        customerId = arguments?.getString("id")
        list.clear()

        // ✅ ALWAYS initialize adapter first
        personAdapter = PersonAdapter(
            list,
            onAddClick = { position ->
                binding.scrollView.post {
                    binding.scrollView.fullScroll(View.FOCUS_DOWN)
                }
                if (list.size < 5) {
                    list.add(position + 1, PersonModel())
                    personAdapter.notifyItemInserted(position + 1)
                    personAdapter.notifyItemChanged(position)
                } else {
                    Toast.makeText(context, "Maximum 5 person allowed", Toast.LENGTH_SHORT).show()
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
                        personName = it.personName,
                        frontURL = it.frontURL,
                        backURL = it.backURL
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

}