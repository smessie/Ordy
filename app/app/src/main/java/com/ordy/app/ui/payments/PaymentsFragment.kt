package com.ordy.app.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.databinding.FragmentPaymentsBinding

class PaymentsFragment : Fragment() {

    private val viewModel: PaymentsViewModel by viewModels { ApiServiceViewModelFactory(requireContext()) }

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_payments, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsHandlers(this, viewModel)

        return binding.root
    }
}