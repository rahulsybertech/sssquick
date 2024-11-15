package com.ssspvtltd.quick.ui.order.add.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.base.BaseBottomDialog
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.InflateBD
import com.ssspvtltd.quick.databinding.FragmentBottomSheetBinding
import com.ssspvtltd.quick.utils.extension.getViewModel

class BottomSheetFragment : BaseBottomDialog<FragmentBottomSheetBinding,BaseViewModel>() {
    override val inflate: InflateBD<FragmentBottomSheetBinding>
        get() = FragmentBottomSheetBinding::inflate

    override fun initViewModel(): BaseViewModel =getViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}