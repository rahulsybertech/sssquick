package com.ssspvtltd.quick_app.ui.order.add.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.BaseBottomDialog
import com.ssspvtltd.quick_app.base.InflateBD
import com.ssspvtltd.quick_app.databinding.FragmentAddItemBottomSheetBinding
import com.ssspvtltd.quick_app.ui.order.add.adapter.PackDataInputAdapter
import com.ssspvtltd.quick_app.ui.order.add.adapter.PackTypeAdapter
import com.ssspvtltd.quick_app.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick_app.utils.extension.getParentFragmentViewModel
import com.ssspvtltd.quick_app.utils.extension.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemBottomSheetFragment :
    BaseBottomDialog<FragmentAddItemBottomSheetBinding, AddItemViewModel>() {
    private val mAdapter by lazy { PackDataInputAdapter(requireContext()) }
    override val inflate: InflateBD<FragmentAddItemBottomSheetBinding>
        get() = FragmentAddItemBottomSheetBinding::inflate

    override fun initViewModel() = getParentFragmentViewModel<AddItemViewModel>()
    override fun isChildFragment(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BottomSheetDialog)
        viewModel.setupForEditBottomSheet()
        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            if(validate()) {
                viewModel.bottomSheetPackData = viewModel.packTypeSuggestions.value?.find {
                    it.value?.equals(spinnerType.text?.toString()) == true
                }

                viewModel.bottomSheetPackQuantity   = etQuantity.text?.toString()
                viewModel.bottomSheetPackAmount     = etAmount.text?.toString()

                if (mAdapter.getList().size > 1) {
                    val packTypeItems = mAdapter.getList().filter {
                        !it.itemID.isNullOrBlank() && !it.itemName.isNullOrBlank()
                    }

                    if (packTypeItems.isNotEmpty()) {
                        viewModel.submitData(packTypeItems)
                    } else {
                        showToast("Please input all fields")
                    }

                } else {

                    if (mAdapter.getList().any { item ->
                            item.itemName.isNullOrBlank() || item.itemID.isNullOrBlank() || (item.itemQuantity ?: "0").trim().toDouble() <= 0
                        }) {


                        showToast("Please select proper Item or qty.")
                    } else {
                        viewModel.submitData(mAdapter.getList())
                    }
                }
            } else {
                showToast("Please input all fields")
            }



        }


        spinnerType.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                if (viewModel.packTypeSuggestions?.value?.any{
                     it.value == binding.spinnerType.text.toString()
                    } == false) {
                    binding.spinnerType.text.clear()
                }
            }

        }

        etAmount.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                tilAmount.isErrorEnabled = false
                etAmount.isCursorVisible = true
            } else {
                if (etAmount.text.isNullOrEmpty()) {
                    tilAmount.isErrorEnabled = true
                    tilAmount.error = "Enter Amount"
                }
            }
        }

        etQuantity.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                tilQuantity.isErrorEnabled = false
                etQuantity.isCursorVisible = true
            } else {
                if (etQuantity.text.isNullOrEmpty()) {
                    tilQuantity.isErrorEnabled = true
                    tilQuantity.error = "Enter Qty."
                }
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

    private fun validate(): Boolean {

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

    }

    companion object {
        fun newInstance() = AddItemBottomSheetFragment()
    }
}