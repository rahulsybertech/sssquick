package com.ssspvtltd.quick_app.ui.order.add.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.ssspvtltd.quick_app.R
import com.ssspvtltd.quick_app.base.BaseActivity
import com.ssspvtltd.quick_app.base.InflateA
import com.ssspvtltd.quick_app.databinding.ActivityAddItemBinding
import com.ssspvtltd.quick_app.model.ARG_ADD_ITEM_LIST
import com.ssspvtltd.quick_app.model.order.add.additem.PackType
import com.ssspvtltd.quick_app.ui.order.add.adapter.PackDataAdapter
import com.ssspvtltd.quick_app.ui.order.add.fragment.AddItemBottomSheetFragment
import com.ssspvtltd.quick_app.ui.order.add.viewmodel.AddItemViewModel
import com.ssspvtltd.quick_app.utils.extension.getViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class   AddItemActivity : BaseActivity<ActivityAddItemBinding, AddItemViewModel>() {
    private val mAdapter by lazy { PackDataAdapter() }
    override val inflate: InflateA<ActivityAddItemBinding> get() = ActivityAddItemBinding::inflate
    override fun initViewModel() = getViewModel<AddItemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getItemsList("709ab0db-0645-427f-a7aa-91b407acc186")
        viewModel.getPackDataList()
        viewModel.getPackType()
        registerObserver()
        initViews()
        registerListener()
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            mAdapter.submitList(viewModel.widgetList)
            if (viewModel.packTypeDataList.isEmpty()) {
                // binding.btnSubmit.isVisible = false
                binding.btnAddMore.setText(R.string.add_item)
            } else {
                // binding.btnSubmit.isVisible = true
                binding.btnAddMore.setText(R.string.add_more)
            }
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { onBackPressedDispatcher.onBackPressed() }
        btnAddMore.setOnClickListener { openAddBottomSheet() }
        mAdapter.onEditClick = { openAddBottomSheet(viewModel.packTypeDataList.indexOf(it)) }
        mAdapter.onDeleteClick = { viewModel.deletePackTypeData(it) }
        btnSubmit.setOnClickListener { sendResultBack() }
        onBackPressedDispatcher.addCallback(
            this@AddItemActivity, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    sendResultBack()
                }
            })
    }

    private fun sendResultBack() {
        setResult(Activity.RESULT_OK, Intent().apply {
            Log.i("TaG","addItemActivity parse Data --==-=-=-=-=-=-${viewModel.packTypeDataList}")
            putParcelableArrayListExtra(ARG_ADD_ITEM_LIST, ArrayList(viewModel.packTypeDataList))
        })
        finish()
    }

    private fun openAddBottomSheet(index: Int = -1) {
        viewModel.bottomSheetIndex = index
        AddItemBottomSheetFragment.newInstance()
            .show(supportFragmentManager, AddItemBottomSheetFragment::class.simpleName)
    }

    companion object {
        @JvmStatic
        fun newStartIntent(context: Context, list: ArrayList<PackType>?): Intent {
            return Intent(context, AddItemActivity::class.java).apply {
                putParcelableArrayListExtra(ARG_ADD_ITEM_LIST, list)
            }
        }
    }
}