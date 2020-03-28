package com.ordy.app.ui.orders.overview.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ordy.app.R
import com.ordy.app.databinding.FragmentOrderUsersBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.users.OrderUsersHandlers

class OrderUsersFragment : Fragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_orde  r_users, container, false)

        // Create binding for the fragment.
        val binding = FragmentOrderUsersBinding.inflate(inflater, container, false)
        binding.handlers = OrderUsersHandlers(this, viewModel)

        return binding.root
    }
}