package com.ordy.app.ui.payments.debtors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.databinding.FragmentPaymentsDebtorsBinding
import com.ordy.app.ui.payments.PaymentsViewModel

class PaymentsDebtorsFragment : Fragment() {

    private val viewModel: PaymentsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflater.inflate(R.layout.fragment_payments_debtors, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsDebtorsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsDebtorsHandlers(this, viewModel)

        return binding.root
    }
}