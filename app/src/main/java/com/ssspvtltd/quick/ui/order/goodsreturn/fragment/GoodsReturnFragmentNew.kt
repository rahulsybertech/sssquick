package com.ssspvtltd.quick.ui.order.goodsreturn.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssspvtltd.quick.base.BaseFragment
import com.ssspvtltd.quick.base.InflateF
import com.ssspvtltd.quick.databinding.FragmentGoodsReturnBinding
import com.ssspvtltd.quick.ui.order.goodsreturn.adapter.GoodsReturnAdapter
import com.ssspvtltd.quick.ui.order.goodsreturn.adapter.GoodsReturnAdapterNew
import com.ssspvtltd.quick.ui.order.goodsreturn.viewmodel.GoodsReturnViewModel
import com.ssspvtltd.quick.utils.extension.getViewModel
import com.ssspvtltd.quick.utils.extension.textChanges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GoodsReturnFragmentNew : Fragment() {

    private var _binding: FragmentGoodsReturnBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: GoodsReturnAdapterNew

    private val viewModel: GoodsReturnViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoodsReturnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadDummyJson() // Or actual API call
    //    viewModel.getGoodsReturn()

        binding.toolbar.apply {
            setTitle("Pending GR")
            setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        }
        adapter = GoodsReturnAdapterNew { item ->
            val convertedItem = com.ssspvtltd.quick.model.order.goodsreturn.GoodsReturnItem(
                id = item.id!!,
                saleBillNo = item.saleBillNo,
                itemName = item.itemName,
                qty = item.qty,
                amount = item.amount,
                salePartyName = item.salePartyName,
                subPartyName = item.subPartyName,
                supplierName = item.supplierName,
                supplierMob = item.supplierMob,
                salePartyMob = item.salePartyMob,
                changedQty = item.qty,
                grInDate = item.salePartyMob,
                noChangeQty = item.changedQty,
                remark = item.remark,
                salePartyEmail = item.remark,
                status = item.status,
            )
           GoodsReturnBottomSheetFragment.newInstance(convertedItem,"")
                .show(childFragmentManager, "GoodsReturnBottomSheet")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.goodsReturnListnew.observe(viewLifecycleOwner) { data ->
            adapter.setData(data)
        }
        registerListener()

       binding.etSearch.textChanges()     // Assuming you're using kotlinx-coroutines bindings
            .debounce(100)
            .onEach {
                viewModel.searchValue = it.toString()
                viewModel.prepareFilteredListGr()
            }
            .launchIn(lifecycleScope)

        // Fetch data
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerListener() = with(binding) {
        toolbar.setNavigationClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }
        etSearch.textChanges().debounce(100).onEach {
            viewModel.searchValue = binding.etSearch.text.toString()
            viewModel.prepareFilteredListGr()
        }.launchIn(lifecycleScope)
       /* adapter.onItemClick = {
            GoodsReturnBottomSheetFragment.newInstance(it)
                .show(childFragmentManager, GoodsReturnBottomSheetFragment::class.simpleName)

        }*/
    }

}
