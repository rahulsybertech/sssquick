package com.ssspvtltd.quick.ui.order.goodsreturn.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentGoodsReturnBinding
import com.ssspvtltd.quick.ui.order.goodsreturn.adapter.GoodsReturnAdapter
import com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel.GoodsReturnViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GoodsReturnSecondaryFragment : BaseFragment<FragmentGoodsReturnBinding, GoodsReturnViewModel>() {
    private var id: String? = null
    private val mAdapter by lazy { GoodsReturnAdapter() }
    override val inflate: InflateF<FragmentGoodsReturnBinding>
        get() = FragmentGoodsReturnBinding::inflate

    override fun initViewModel(): GoodsReturnViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.toolbar.setNavigationClickListener {
        //     if (requireActivity() is MainActivity) findNavController().navigateUp()
        //     else requireActivity().onBackPressed()
        // }
         id = arguments?.getString("ID")
        if (!id.isNullOrEmpty()) {
            viewModel.getGoodsReturnSecondary(id!!)
        }
        binding.toolbar.apply {
            //  setTitle("Goods Return")
            setTitle("Pending GR")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
        initViews()
        registerListener()
    }

    private fun registerObserver() {
        viewModel.isListAvailable.observe(this) {
            mAdapter.submitList(viewModel.widgetList)
        }
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = mAdapter
        toolbar.setTitle("Pending GR")
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredList()
        }.launchIn(lifecycleScope)
        mAdapter.onItemClick = {
            val id =arguments?.getString("ID").toString()
            GoodsReturnBottomSheetFragment.newInstance(it,  id)
                .show(childFragmentManager, GoodsReturnBottomSheetFragment::class.simpleName)

        }
    }
    companion object {
        private const val ARG_ID = "ID"

        fun newInstance(id: String?): GoodsReturnSecondaryFragment {
            val fragment = GoodsReturnSecondaryFragment()
            val args = Bundle()
            args.putString(ARG_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
