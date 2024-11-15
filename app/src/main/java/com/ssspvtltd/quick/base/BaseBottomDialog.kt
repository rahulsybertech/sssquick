package com.ssspvtltd.quick.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import tech.developingdeveloper.toaster.Toaster

typealias InflateBD<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseBottomDialog<VB : ViewBinding, VM : BaseViewModel> :
    BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected lateinit var viewModel: VM

    abstract val inflate: InflateBD<VB>
    abstract fun initViewModel(): VM
    open fun isChildFragment(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = initViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BaseActivity<*, *>)?.takeIf { !isChildFragment() }?.let {
            viewModel.setErrorObserver(viewLifecycleOwner, it.errorObserver)
            viewModel.setAlertMsgObserver(viewLifecycleOwner, it.alertMsgObserver)
            viewModel.setProgressBarObserver(viewLifecycleOwner, it.progressbarObserver)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = true) {
        (activity as? BaseActivity<*, *>)?.addFragment(fragment, container, addToBackStack)
    }

    fun replaceFragment(fragment: Fragment, container: Int, addToBackStack: Boolean = true) {
        (activity as? BaseActivity<*, *>)?.replaceFragment(fragment, container, addToBackStack)
    }

    fun showToast(message: String?, duration: Int = Toaster.LENGTH_SHORT) {
        (activity as? BaseActivity<*, *>)?.showToast(message, duration)
    }

    fun showSnackBar(message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
        (activity as? BaseActivity<*, *>)?.showSnackBar(message, duration)
    }
}