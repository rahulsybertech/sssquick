package com.ssspvtltd.quick_new.ui.order.add.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.ssspvtltd.quick_new.R
import com.ssspvtltd.quick_new.base.BaseBottomDialog
import com.ssspvtltd.quick_new.base.InflateBD
import com.ssspvtltd.quick_new.databinding.FragmentAddItemBottomSheetBinding
import com.ssspvtltd.quick_new.ui.order.add.adapter.PackDataInputAdapter
import com.ssspvtltd.quick_new.ui.order.add.adapter.PackTypeAdapter
import com.ssspvtltd.quick_new.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick_new.utils.extension.getParentFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemBottomSheetFragment :
    BaseBottomDialog<FragmentAddItemBottomSheetBinding, AddItemViewModel>() {
    private val mAdapter by lazy { PackDataInputAdapter() }
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
        registerListeners()
    }

    private fun registerObserver() {
        viewModel.packTypeItemSuggestions.observe(this) {
            mAdapter.setSuggestions(it.orEmpty())
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
            viewModel.bottomSheetPackData = viewModel.packTypeSuggestions.value?.find {
                it.value?.equals(spinnerType.text?.toString()) == true
            }

            viewModel.bottomSheetPackQuantity   = etQuantity.text?.toString()
            viewModel.bottomSheetPackAmount     = etAmount.text?.toString()


            Log.i("TaG","0000000000000000000  ${mAdapter.getList()}")
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


                    showToast("Please input all fields")
                } else {
                    viewModel.submitData(mAdapter.getList())
                }
            }


        }
        spinnerType.doOnTextChanged { text, _, _, _ ->
            viewModel.bottomSheetPackData = viewModel.packTypeSuggestions.value?.find {
                it.value?.equals(text?.toString()) == true
            }
        }
        etQuantity.doOnTextChanged { text, _, _, _ ->
            viewModel.bottomSheetPackQuantity = text?.toString()
        }
        etAmount.doOnTextChanged { text, _, _, _ ->
            viewModel.bottomSheetPackAmount = text?.toString()
        }


    }

    companion object {
        fun newInstance() = AddItemBottomSheetFragment()
    }
}