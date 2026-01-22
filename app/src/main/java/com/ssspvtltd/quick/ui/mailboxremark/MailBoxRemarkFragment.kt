package com.ssspvtltd.quick.ui.mailboxremark
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentMailBoxBinding
import com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel.GoodsReturnViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
@AndroidEntryPoint
class MailBoxRemarkFragment  : BaseFragment<FragmentMailBoxBinding, GoodsReturnViewModel>() {
    private val mAdapter by lazy { MailBoxAdapter() }
    override val inflate: InflateF<FragmentMailBoxBinding>
        get() = FragmentMailBoxBinding::inflate

    override fun initViewModel(): GoodsReturnViewModel = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getMailBox()
        registerObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.toolbar.setNavigationClickListener {
        //     if (requireActivity() is MainActivity) findNavController().navigateUp()
        //     else requireActivity().onBackPressed()
        // }
        binding.toolbar.apply {
            //  setTitle("Goods Return")
            setTitle("Mail Box GR")
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
        toolbar.setTitle("Mail Box")
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredMailBoxList()
        }.launchIn(lifecycleScope)
        mAdapter.onItemClick = {
          /*  val intent = Intent(requireActivity(), GoodReturnSecondaryActivity::class.java)
            intent.putExtra("ID",it.id)
            startActivity(intent)*/
            /*GoodsReturnBottomSheetFragment.newInstance(it)
                .show(childFragmentManager, GoodsReturnBottomSheetFragment::class.simpleName)*/

        }
    }
}