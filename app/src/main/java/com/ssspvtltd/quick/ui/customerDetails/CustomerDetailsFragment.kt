package com.ssspvtltd.quick.ui.customerDetails

import android.Manifest
import android.R
import android.content.Intent
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
import com.ssspvtltd.quick.model.customerdetails.PersonModel
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
  //  private val mAdapter by lazy { GoodsReturnAdapter() }
    private lateinit var personAdapter: PersonAdapter
    private val list = mutableListOf<PersonModel>()
    override val inflate: InflateF<FragmentCustomerDetailsBinding>
    get() = FragmentCustomerDetailsBinding::inflate
    private var isNickNameSelected = false
    private var selectedPosition = -1
    private var selectedImageType = ""
    private var selectedNickNameId = ""
    private var selectedCustomerId = ""
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

        // binding.toolbar.setNavigationClickListener {
        //     if (requireActivity() is MainActivity) findNavController().navigateUp()
        //     else requireActivity().onBackPressed()
        // }
        binding.toolbar.apply {
            //  setTitle("Goods Return")
            setTitle("Customer Details")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
        initViews()
        registerObserver()
        viewModel.factchCustomerNickNameList()
        viewModel.getCustomerList()

        binding.tvAdd.setOnClickListener {

            val personList = list.map {

                Person(
                    id = null,
                    personName = it.personName ?: "",
                    aadharFrontBase64 = it.aadharFrontBase64 ?: "",
                    aadharBackBase64 = it.aadharBackBase64 ?: "",
                    frontURL = "",
                    backURL = ""
                )
            }
            val request = CustomerDetailsRequest(

                id = null,
                nickNameID = selectedNickNameId,
                accountID = selectedCustomerId,
                mobileNo = binding.etMobileNumber.text.toString(),
                marketerID = null,
                nickName = binding.dropNickName.text.toString(),
                accountName = binding.dropCity.text.toString(),
                date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(
                    Date()
                ),
                persons = personList
            )
            viewModel.addCustomerDetailReq(request)

           /* val gson = Gson()

            val jsonString = gson.toJson(request)
            showToast(jsonString.toString())*/
         //   Log.e("Req",request.toString())

        }

    }

    private fun registerObserver() {



        viewModel.customerNickNameList.observe(viewLifecycleOwner) { list ->
            nickNameData = list
            val nickNameList = list.map { it.name }
            val nickAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nickNameList
            )
            binding.dropNickName.setAdapter(nickAdapter)
        }
        binding.dropNickName.setOnItemClickListener { parent, view, position, id ->

            val selectedNick = nickNameData[position]

            val nickId = selectedNick.id
            selectedNickNameId=nickId
            isNickNameSelected=true

            // Call API with ID
            viewModel.factchCustomerListByNickNameId(nickId)

        }
        binding.dropNickName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {

                    isNickNameSelected = false

                    // NickName cleared → call default customer API
                    viewModel.getCustomerList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }


        })


        viewModel.customerListByNickNameId.observe(viewLifecycleOwner) { list ->
            val customerList = list.map { it.name }
            val customerAdapterByNickNameId = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                customerList
            )
            binding.dropCity.setAdapter(customerAdapterByNickNameId)
        }

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

        binding.dropCity.setOnItemClickListener { parent, view, position, id ->

            val selectedCustomer = customerData[position]
            val nickId = selectedCustomer.nickNameID
             selectedNickNameId = selectedCustomer.nickNameID
             selectedCustomerId = selectedCustomer.id
            val nickName = selectedCustomer.nickName

            // Correct way
            binding.dropNickName.setText(nickName, false)
        }


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



    private fun initViews() = with(binding) {

        toolbar.setTitle("Customer Details")

        list.clear()
        list.add(PersonModel())

        personAdapter = PersonAdapter(
            list,
            onAddClick = { position ->

                list.add(position + 1, PersonModel())
                personAdapter.notifyItemInserted(position + 1)

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