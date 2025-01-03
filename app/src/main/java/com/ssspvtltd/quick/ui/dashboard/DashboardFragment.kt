package com.ssspvtltd.quick.ui.dashboard

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BuildCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.ssspvtltd.quick.BuildConfig
import com.ssspvtltd.quick.R
import com.ssspvtltd.quick.databinding.FragmentDashboardBinding
import com.ssspvtltd.quick.model.DashBoardDataResponse
import com.ssspvtltd.quick.model.version.CheckVersionResponse
import com.ssspvtltd.quick.ui.main.MainActivity
import com.ssspvtltd.quick.ui.order.add.viewmodel.DashBoardViewmodel
import com.ssspvtltd.quick.utils.amountFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashBoardViewmodel by viewModels()
    private val dateFormat = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())


    companion object {
        const val TAG = "TaG"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModelObservers()
        setClickListeners()
        setMyView()
        callApis()

    }

    private fun setViewModelObservers() {
        viewModel.dashBoardDetailsLiveData.observe(viewLifecycleOwner, dashBoardObserver)
        viewModel.dashBoardSaleCountDetailsLiveData.observe(viewLifecycleOwner, dashBoardObserver2)
        viewModel.fetchCheckVersion.observe(viewLifecycleOwner, versionObserver)
    }

    private fun callApis() {
        viewModel.getDashBoardDetails()
        viewModel.getDashBoardSaleCountDetails(
            binding.txtTotalSaleFromDate.text.toString(), binding.txtTotalSaleToDate.text.toString()
        )// one week from current

        viewModel.checkVersionAPI("SSSQUICK")



    }

    private fun setMyView() {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val currentDate = LocalDate.now()
        val oneWeekBeforeDate = currentDate.minusWeeks(1L)

        binding.txtTotalSaleFromDate.text = oneWeekBeforeDate.format(formatter)
        binding.txtTotalSaleToDate.text = currentDate.format(formatter)

    }


    private fun setClickListeners() {

        binding.toolbar.apply {
            setNavigationClickListener {
                (requireActivity() as? MainActivity)?.openDrawer()
            }
            setTitle("Dashboard")
        }

        binding.llFromDate.setOnClickListener {
            showFromDatePicker()
        }

        binding.llToDate.setOnClickListener {
            showToDatePicker()
        }

        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.deep_orange_800))
        binding.swipeRefreshLayout.setOnRefreshListener {
            callApis()
            binding.swipeRefreshLayout.isRefreshing = false

        }

    }

    private val versionObserver = Observer<CheckVersionResponse.Data?>{versionResponse ->
        if (versionResponse?.versionName!! == BuildConfig.VERSION_NAME){

        }
    }


    private val dashBoardObserver = Observer<DashBoardDataResponse.Data?> { dashBoardData ->

        with(binding) {
            tvTodaysorders.text = (dashBoardData?.todayOrderCount ?: 0).toString()
            tvHoldOrders.text = (dashBoardData?.totalHoldOrderCount ?: 0).toString()
            tvPendingOrders.text = (dashBoardData?.totalPendingOrderCount ?: 0).toString()
            tvPendingGR.text = (dashBoardData?.totalGRCount ?: 0).toString()
        }

    }
    private val dashBoardObserver2 = Observer<DashBoardDataResponse.Data?> { dashBoardData ->
        with(binding) {
            tvTotalSale.text = (dashBoardData?.totalSaleCount ?: 0).toString()
            println("GET_AMOUNT ${dashBoardData?.totalSaleAmt}")
            if (dashBoardData?.totalSaleAmt == null) {
                tvTotalSaleAmount.text = "0.0"
            } else {
                tvTotalSaleAmount.text = amountFormat((dashBoardData.totalSaleAmt).toString())
            }
        }
    }

    private fun showFromDatePicker() {
        val fromDate = Calendar.getInstance()

        // Check if there's already a selected "From Date"
        if (!binding.txtTotalSaleFromDate.text.isNullOrEmpty()) {
            fromDate.time = dateFormat.parse(binding.txtTotalSaleFromDate.text.toString())!!
        }

        val fromDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                fromDate.set(Calendar.YEAR, year)
                fromDate.set(Calendar.MONTH, monthOfYear)
                fromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.txtTotalSaleFromDate.text = dateFormat.format(fromDate.time)

                // Check if ToDate needs to be updated based on the selected FromDate
                val currentToDate = Calendar.getInstance().apply {
                    time = dateFormat.parse(binding.txtTotalSaleToDate.text.toString())!!
                }
                if (currentToDate.before(fromDate)) {
                    val updatedToDate = fromDate.clone() as Calendar
                    updatedToDate.add(Calendar.DAY_OF_YEAR, 0)
                    binding.txtTotalSaleToDate.text = dateFormat.format(updatedToDate.time)
                }
                viewModel.getDashBoardSaleCountDetails(
                    binding.txtTotalSaleFromDate.text.toString(),
                    binding.txtTotalSaleToDate.text.toString()
                ) // Refresh sale count details

            }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            fromDateListener,
            fromDate.get(Calendar.YEAR),
            fromDate.get(Calendar.MONTH),
            fromDate.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict the "From Date" to today or earlier
        val today = Calendar.getInstance()
        datePickerDialog.datePicker.maxDate = today.timeInMillis

        datePickerDialog.show()
    }

    private fun showToDatePicker() {
        val toDate = Calendar.getInstance()

        // Check if there's already a selected "To Date"
        if (!binding.txtTotalSaleToDate.text.isNullOrEmpty()) {
            toDate.time = dateFormat.parse(binding.txtTotalSaleToDate.text.toString())!!
        }

        val toDateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                toDate.set(Calendar.YEAR, year)
                toDate.set(Calendar.MONTH, monthOfYear)
                toDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.txtTotalSaleToDate.text = dateFormat.format(toDate.time)
                viewModel.getDashBoardSaleCountDetails(
                    binding.txtTotalSaleFromDate.text.toString(),
                    binding.txtTotalSaleToDate.text.toString()
                ) // Refresh sale count details
            }

        // Retrieve the selected "From Date" or use today's date if not selected
        val fromDate = Calendar.getInstance().apply {
            time = if (!binding.txtTotalSaleFromDate.text.isNullOrEmpty()) {
                dateFormat.parse(binding.txtTotalSaleFromDate.text.toString())!!
            } else {
                Calendar.getInstance().time
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            toDateListener,
            toDate.get(Calendar.YEAR),
            toDate.get(Calendar.MONTH),
            toDate.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict the "To Date" to be no earlier than the "From Date" and no later than today
        datePickerDialog.datePicker.minDate = fromDate.timeInMillis
        val today = Calendar.getInstance()
        datePickerDialog.datePicker.maxDate = today.timeInMillis

        datePickerDialog.show()
    }

}
