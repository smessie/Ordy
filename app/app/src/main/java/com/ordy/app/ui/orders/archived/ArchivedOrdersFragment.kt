package com.ordy.app.ui.orders.archived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.databinding.FragmentOrdersArchivedBinding
import com.ordy.app.ui.orders.OrdersViewModel

class ArchivedOrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by activityViewModels { ApiServiceViewModelFactory(requireContext()) }

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create binding for the fragment.
        val binding = FragmentOrdersArchivedBinding.inflate(inflater, container, false)
        binding.handlers = ArchivedOrdersHandlers(this, viewModel)

        return binding.root
    }
}