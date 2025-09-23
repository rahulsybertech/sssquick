package com.ssspvtltd.quick.ui.order.add.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentAddItemBottomSheetBinding
import com.ssspvtltd.quick.ui.order.add.adapter.PackDataInputAdapter
import com.ssspvtltd.quick.ui.order.add.adapter.PackTypeAdapter
import com.ssspvtltd.quick.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick.utils.extension.getParentFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemBottomSheetFragment :
    BaseBottomDialog<FragmentAddItemBottomSheetBinding, AddItemViewModel>() {
    private val mAdapter by lazy { PackDataInputAdapter(requireContext()) }
    private val isGrMode by lazy {
        arguments?.getBoolean("isGrMode") ?: false
    }
    override val inflate: InflateBD<FragmentAddItemBottomSheetBinding>
        get() = FragmentAddItemBottomSheetBinding::inflate

    override fun initViewModel() = getParentFragmentViewModel<AddItemViewModel>()
    override fun isChildFragment(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BottomSheetDialog)
        viewModel.setupForEditBottomSheet()
        registerObserver()
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setCanceledOnTouchOutside(false) // 👈 Prevents dismissal on outside tap
        return dialog
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isGrMode) {
            binding.tilPackingType.visibility = View.GONE
            binding.tilQuantity.visibility = View.GONE
            binding.tilAmount.visibility = View.GONE
        } else {
            binding.tilPackingType.visibility = View.VISIBLE
            binding.tilQuantity.visibility = View.VISIBLE
            binding.tilAmount.visibility = View.VISIBLE
        }
        binding.recyclerView.adapter = mAdapter
        binding.spinnerType.isFocusable = false
        registerListeners()
    }

    private fun registerObserver() {
        viewModel.packTypeItemSuggestions.observe(this) {
            mAdapter.setSuggestions(it.orEmpty())
            viewModel.hideProgressBar()
        }
        viewModel.packTypeSuggestions.observe(this) {
            val packTypeAdapter = PackTypeAdapter(requireContext(), R.layout.item_saleparty, it.orEmpty())
            binding.spinnerType.setAdapter(packTypeAdapter)
        }
        viewModel.bottomSheetPrefilledPackType.observe(this) {
            val selectedPackTypeIndex = viewModel.packTypeSuggestions.value
                ?.indexOfFirst { it1 -> it.pcsId == it1.id } ?: -1
            if (selectedPackTypeIndex > -1) {
                binding.spinnerType.selectedPosition = selectedPackTypeIndex
            }
            binding.etAmount.setText(it.amount)
            binding.etQuantity.setText(it.qty)
            it.itemDetail?.let { it1 -> mAdapter.submitList(it1)}
            binding.addItem.setText(R.string.update)
        }
        viewModel.bottomSheetSubmitted.observe(this) {
            if (it) {
                viewModel.bottomSheetIndex = -1
                this@AddItemBottomSheetFragment.dismiss()
            }
        }
    }



    private fun registerListeners() = with(binding) {
        closeDialog.setOnClickListener { dismiss() }

        addItem.setOnClickListener {
            if (validate()) {
                if (!isGrMode) {
                    viewModel.bottomSheetPackData = viewModel.packTypeSuggestions.value?.find {
                        it.value?.equals(spinnerType.text?.toString()) == true
                    }
                    viewModel.bottomSheetPackQuantity = etQuantity.text?.toString()
                    viewModel.bottomSheetPackAmount = etAmount.text?.toString()
                }

                val validItems = mAdapter.getList().filter {
                    !it.itemID.isNullOrBlank() && !it.itemName.isNullOrBlank() &&
                            (it.itemQuantity?.trim()?.toDoubleOrNull() ?: 0.0) > 0.0
                }

                if (validItems.isEmpty()) {
                    showToast("Please select proper item and quantity")
                } else {
                    viewModel.submitData(validItems,isGrMode)
                }
            } else {
                showToast("Please input all fields")
            }
        }

        if (!isGrMode) {
            spinnerType.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && viewModel.packTypeSuggestions?.value?.none {
                        it.value == binding.spinnerType.text.toString()
                    } == true) {
                    binding.spinnerType.text.clear()
                }
            }

            etAmount.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    tilAmount.isErrorEnabled = false
                    etAmount.isCursorVisible = true
                } else if (etAmount.text.isNullOrEmpty()) {
                    tilAmount.isErrorEnabled = true
                    tilAmount.error = "Enter Amount"
                }
            }

            etQuantity.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    tilQuantity.isErrorEnabled = false
                    etQuantity.isCursorVisible = true
                } else if (etQuantity.text.isNullOrEmpty()) {
                    tilQuantity.isErrorEnabled = true
                    tilQuantity.error = "Enter Qty."
                }
            }

            spinnerType.doOnTextChanged { text, _, _, _ ->
                binding.tilPackingType.isErrorEnabled = false
                viewModel.bottomSheetPackData = viewModel.packTypeSuggestions.value?.find {
                    it.value?.equals(text?.toString()) == true
                }
            }

            etQuantity.doOnTextChanged { text, _, _, _ ->
                binding.tilQuantity.isErrorEnabled = false
                viewModel.bottomSheetPackQuantity = text?.toString()
            }

            etAmount.doOnTextChanged { text, _, _, _ ->
                binding.tilAmount.isErrorEnabled = false
                viewModel.bottomSheetPackAmount = text?.toString()
            }
        }
    }

  /*  private fun validate(): Boolean {
        if (!isGrMode) {
            // Validate only if NOT in GR Mode
            if (binding.etQuantity.text.isNullOrEmpty()) {
                binding.tilQuantity.isErrorEnabled = true
                binding.etQuantity.isCursorVisible = true
                binding.tilQuantity.error = "Enter Qty."
            }

            if (binding.etAmount.text.isNullOrEmpty()) {
                binding.tilAmount.isErrorEnabled = true
                binding.etAmount.isCursorVisible = true
                binding.tilAmount.error = "Enter Amount"
            }

            if (binding.spinnerType.text.isNullOrEmpty()) {
                binding.tilPackingType.isErrorEnabled = true
                binding.tilPackingType.error = "Select pack type"
            }
        }

        return if (mAdapter.getList().isNotEmpty()) {
            val isValidItems = mAdapter.getList().none { item ->
                item.itemName.isNullOrBlank() ||
                        item.itemID.isNullOrBlank() ||
                        (item.itemQuantity?.trim()?.toDoubleOrNull() ?: 0.0) <= 0.0
            }
            isValidItems
        } else {
            false
        }
    }*/
  private fun validate(): Boolean {
      if (!isGrMode) {
          // Validate only if NOT in GR Mode
          if (binding.etQuantity.text.isNullOrEmpty()) {
              binding.tilQuantity.isErrorEnabled = true
              binding.etQuantity.isCursorVisible = true
              binding.tilQuantity.error = "Enter Qty."
          }

          if (binding.etAmount.text.isNullOrEmpty()) {
              binding.tilAmount.isErrorEnabled = true
              binding.etAmount.isCursorVisible = true
              binding.tilAmount.error = "Enter Amount"
          }

          if (binding.spinnerType.text.isNullOrEmpty()) {
              binding.tilPackingType.isErrorEnabled = true
              binding.tilPackingType.error = "Select pack type"
          }
      }
      val isAnyFieldIncomplete = mAdapter.getList().any {
          (it.itemName.isNullOrBlank() && !it.itemQuantity.isNullOrBlank()) ||
                  (!it.itemName.isNullOrBlank() && it.itemQuantity.isNullOrBlank())
      }

      if (isAnyFieldIncomplete) {
        //  Toast.makeText(context, "Please fill both item name and quantity", Toast.LENGTH_SHORT).show()
          return false
      }

// ✅ If all fields are filled, proceed
      val filteredList = mAdapter.getList().filterNot {
          it.itemName.isNullOrBlank() && it.itemQuantity.isNullOrBlank()
      }
      // Remove items where both name and quantity are empty
      if(mAdapter.getList().size>1){
          // Update adapter with filtered list
          mAdapter.setList(filteredList)
      }else{

      }

      // Proceed with validation only if list is not empty
      return if (filteredList.isNotEmpty()) {
          val isValidItems = filteredList.none { item ->
              item.itemName.isNullOrBlank() ||
                      item.itemID.isNullOrBlank() ||
                      (item.itemQuantity?.trim()?.toDoubleOrNull() ?: 0.0) <= 0.0
          }
          isValidItems
      } else {
          false
      }
  }



    /*private fun validate(): Boolean {

        if (binding.etQuantity.text.isNullOrEmpty()) {
            binding.tilQuantity.isErrorEnabled = true
            //binding.etQuantity.requestFocus()
            binding.etQuantity.isCursorVisible = true
            binding.tilQuantity.error = "Enter Qty."
        }
        if (binding.etAmount.text.isNullOrEmpty()) {
            binding.tilAmount.isErrorEnabled = true
            //binding.etAmount.requestFocus()
            binding.etAmount.isCursorVisible = true
            binding.tilAmount.error = "Enter Amount"
        }
        if (binding.spinnerType.text.isNullOrEmpty()) {
            binding.tilPackingType.isErrorEnabled = true
            binding.tilPackingType.error = "select pack type"
        }
        if (binding.spinnerType.text.isNullOrEmpty()) {
            binding.tilPackingType.isErrorEnabled = true
            binding.tilPackingType.error = "select pack type"
        }
        return if (mAdapter.getList().isNotEmpty()) {


            ( mAdapter.getList().any { item ->
                                                item.itemName.isNullOrBlank() ||
                                                item.itemID.isNullOrBlank() ||
                                                item.itemQuantity.isNotNullOrBlank()

                                        } && !binding.etQuantity.text.isNullOrEmpty() && !binding.etAmount.text.isNullOrEmpty()
                //   make focus on empty or required field  argent

            )
        } else {
            false
        }

    }*/

    companion object {
        fun newInstance(isGrMode: Boolean): AddItemBottomSheetFragment {
            val fragment = AddItemBottomSheetFragment()
            val bundle = Bundle()
            bundle.putBoolean("isGrMode", isGrMode)
            fragment.arguments = bundle
            return fragment
        }
    }
}