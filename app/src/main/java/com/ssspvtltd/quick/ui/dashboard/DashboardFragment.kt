package com.ssspvtltd.quick.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssspvtltd.quick.ui.main.MainActivity
import com.ssspvtltd.quick.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            setNavigationClickListener{
                (requireActivity() as? MainActivity)?.openDrawer()
            }
            setTitle("Dashboard")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
