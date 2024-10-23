package com.ssspvtltd.quick_new.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ssspvtltd.quick_new.databinding.FragmentDashboardBinding
import com.ssspvtltd.quick_new.model.DashBoardDataResponse
import com.ssspvtltd.quick_new.ui.main.MainActivity
import com.ssspvtltd.quick_new.ui.order.add.viewmodel.DashBoardViewmodel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel : DashBoardViewmodel by viewModels()



    companion object{
        const val TAG = "TaG"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModelObservers()
        setClickListeners()

        viewModel.getDashBoardDetails()
        viewModel.getDashBoardSaleCountDetails("01/03/2024","01/03/2025")

    }

    private fun setViewModelObservers() {
        viewModel.dashBoardDetailsLiveData.observe(viewLifecycleOwner, dashBoardObserver)
        viewModel.dashBoardSaleCountDetailsLiveData.observe(viewLifecycleOwner, dashBoardObserver2)



    }


    private fun setClickListeners() {

        binding.toolbar.apply {
            setNavigationClickListener{
                (requireActivity() as? MainActivity)?.openDrawer()
            }
            setTitle("Dashboard")
        }

        binding.ivDateFilter.setOnClickListener {
            showDateRangePicker()

        }

    }


    private val dashBoardObserver = Observer<DashBoardDataResponse.Data?> { dashBoardData ->
        Log.i(TAG, "observer 1 -=-=-= :$dashBoardData ")
        with(binding){
            tvTodaysorders.text     = (dashBoardData?.todayOrderCount ?: 0).toString()
            tvHoldOrders.text       = (dashBoardData?.totalHoldOrderCount ?: 0).toString()
            tvPendingOrders.text    = (dashBoardData?.totalPendingOrderCount ?: 0).toString()
            tvPendingGR.text        = (dashBoardData?.totalGRCount ?: 0).toString()
            tvTotalSale.text        = (dashBoardData?.totalSaleCount ?: 0).toString()
        }

    }
    private val dashBoardObserver2 = Observer<DashBoardDataResponse.Data?> { dashBoardData ->
        Log.i(TAG,"observer 2 response -=-=-=-=-===-=> ${dashBoardData}")
        /*with(binding){
            tvTodaysorders.text     = (dashBoardData?.todayOrderCount ?: 0).toString()
            tvHoldOrders.text       = (dashBoardData?.totalHoldOrderCount ?: 0).toString()
            tvPendingOrders.text    = (dashBoardData?.totalPendingOrderCount ?: 0).toString()
            tvPendingGR.text        = (dashBoardData?.totalGRCount ?: 0).toString()
            tvTotalSale.text        = (dashBoardData?.totalSaleCount ?: 0).toString()
        }*/

    }


    private fun showDateRangePicker() {

       /* val constraintsBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())


        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                                .setTitleText("Select Date Range")
                                .setCalendarConstraints(constraintsBuilder.build())
                                .build()


        dateRangePicker.show(parentFragmentManager, "date_range_picker")


        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection?.first ?: 0L
            val endDate = selection?.second ?: 0L

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedStartDate = dateFormat.format(Date(startDate))
            val formattedEndDate = dateFormat.format(Date(endDate))

            binding.txtTotalSaleFromDate.text   = formattedStartDate
            binding.txtTotalSaleToDate.text     = formattedEndDate
        }*/
    }


}
