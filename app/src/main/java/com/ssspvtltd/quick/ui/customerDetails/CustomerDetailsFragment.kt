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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
  //  private val mAdapter by lazy { GoodsReturnAdapter() }
    private lateinit var personAdapter: PersonAdapter
    private val list = mutableListOf<PersonModel>()
    override val inflate: InflateF<FragmentCustomerDetailsBinding>
    get() = FragmentCustomerDetailsBinding::inflate
    private var isNickNameSelected = false
    private var isCustomerNameSelected = false
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
                    } else {
                        list[selectedPosition].aadharBackBitmap = it
                        list[selectedPosition].aadharBackBase64 = base64
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
                    } else {
                        list[selectedPosition].aadharBackBitmap = bitmap
                        list[selectedPosition].aadharBackBase64 = base64
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
        registerObserver()
        viewModel.showProgressBar()
        viewModel.factchCustomerNickNameList()
        viewModel.getCustomerList()

        binding.tvAdd.setOnClickListener {

            if (!validateForm()) return@setOnClickListener

            val personList = list.map {
                if(customerId!!.isNotEmpty()){
                    Person(
                        id = it.id,
                        personName = it.personName,
                        aadharFrontBase64 = it.aadharFrontBase64 ?: "",
                        aadharBackBase64 = it.aadharBackBase64 ?: "",
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                }else{
                    Person(
                        id = null,
                        personName = it.personName,
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
            viewModel.addCustomerDetailReq(request)
        }

    }


    private fun registerObserver() {
        viewModel.customerNickNameList.observe(viewLifecycleOwner) { list ->
            viewModel.hideProgressBar()
            nickNameData = list
            val nickNameList = list.map { it.name }
            val nickAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nickNameList
            )
            binding.dropNickName.setAdapter(nickAdapter)
        }
        binding.dropNickName.setOnItemClickListener { _, _, position, _ ->

            val selectedNick = nickNameData[position]
            val nickId = selectedNick.id
            selectedNickNameId=nickId
            isNickNameSelected=true
            selectedNickName=selectedNick.nickName.toString()
            selectedCustomerName=""
            binding.dropNickName.error = null
            binding.dropNickName.clearFocus()
            binding.dropCutomerByNickNameId.setText("", false)
            selectedCustomerId=""
            binding.layouCustomerBYNickNameId.visibility= View.VISIBLE
            binding.layouCustomer.visibility= View.INVISIBLE
            viewModel.factchCustomerListByNickNameId(nickId)

        }
        binding.dropNickName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    selectedNickName=""
                    selectedCustomerName=""
                    isNickNameSelected = false
                    binding.layouCustomerBYNickNameId.visibility= View.INVISIBLE
                    binding.layouCustomer.visibility= View.VISIBLE
                   // viewModel.getCustomerList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
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


        viewModel.customerList.observe(viewLifecycleOwner) { list ->
            customerData=list
            val customerList = list.map { it.name }
            val customerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                customerList
            )
            binding.dropCity.setAdapter(customerAdapter)

        }


        binding.dropCity.setOnItemClickListener { _, _, position, _ ->

            val selectedCustomer = customerData[position]
             selectedNickNameId = selectedCustomer.nickNameID
             selectedCustomerId = selectedCustomer.id
            val nickName = selectedCustomer.nickName
            isNickNameSelected=true
            isCustomerNameSelected=true
            selectedCustomerName=selectedCustomer.name
            selectedNickName=selectedCustomer.nickName

            // Correct way
            binding.layoutNickNameByCustomerId.visibility= View.VISIBLE
            binding.layoutNickName.visibility= View.INVISIBLE
            binding.dropNickNameByCustomerId.setText(nickName, false)
        }

        binding.dropCity.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    binding.layoutNickNameByCustomerId.visibility= View.GONE
                    binding.layoutNickName.visibility= View.VISIBLE
                    isNickNameSelected=false
                    isCustomerNameSelected=false
                    selectedCustomerName=""
                    selectedNickName=""
                    binding.dropNickNameByCustomerId.setText("", false)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        viewModel.addCustomerResult.observe(viewLifecycleOwner) { isSuccess ->

            if (isSuccess) {

                showToast("Customer Added Successfully")

                // Example: Close screen
                findNavController().popBackStack()

            } else {

                showToast("Customer Add Failed")

            }
        }
    }

    private fun validateForm(): Boolean {

        // 1. Nick Name validation
        if (!isNickNameSelected) {
            binding.dropNickName.error = "Select Nick Name"
            binding.dropNickName.requestFocus()
            return false
        }

        // 2. Customer validation
        if (!isCustomerNameSelected) {
            binding.dropCity.error = "Select Customer"
            binding.dropCity.requestFocus()
            return false
        }

        // 3. Mobile validation
        val mobile = binding.etMobileNumber.text.toString().trim()

        if (mobile.isEmpty()) {
            binding.etMobileNumber.error = "Enter Mobile Number"
            binding.etMobileNumber.requestFocus()
            return false
        }

        if (!mobile.matches(Regex("^[6-9][0-9]{9}$"))) {
            binding.etMobileNumber.error = "Enter valid Indian mobile number"
            binding.etMobileNumber.requestFocus()
            return false
        }

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
                showToast("Enter person name at position ${index + 1}")
                return false
            }


            if (person.aadharFrontBase64.isNullOrEmpty()&&person.frontURL.isNullOrEmpty()) {
                showToast("Upload Aadhar Front at position ${index + 1}")
                return false
            }

            if (person.aadharBackBase64.isNullOrEmpty()&&person.frontURL.isNullOrEmpty()) {
                showToast("Upload Aadhar Back at position ${index + 1}")
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
                if (list.size < 5) {
                    list.add(position + 1, PersonModel())
                    personAdapter.notifyItemInserted(position + 1)
                } else {
                    Toast.makeText(context, "Maximum 6 person allowed", Toast.LENGTH_SHORT).show()
                }
            },
            onRemoveClick = { position ->
                if (list.size > 1) {
                    list.removeAt(position)
                    personAdapter.notifyItemRemoved(position)
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
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = personAdapter

        // ✅ NOW handle data
        if (!customerId.isNullOrEmpty()) {

            toolbar.setTitle("Edit Customer Details")

            viewModel.fatchAccountDetailsForID(customerId!!)

            viewModel.accountDetailsForID.observe(viewLifecycleOwner) { list1 ->

                val data = list1[0]

                binding.dropNickName.setText(data.nickName, false)
                isCustomerNameSelected=true
                isNickNameSelected=true
                selectedNickNameId=data.nickNameID
                selectedCustomerId=data.accountID
                customerId=data.id

                binding.dropCity.setText(data.accountName, false)
                binding.etMobileNumber.setText(data.mobileNo)

                val persons = data.persons.map {
                    PersonModel(
                        id = it.id,
                        personName = it.personName,
                        frontURL = it.frontURL,
                        backURL = it.backURL
                    )
                }

                personAdapter.updateList(persons)   // ✅ safe now
            }

        } else {
            toolbar.setTitle("Customer Details")

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