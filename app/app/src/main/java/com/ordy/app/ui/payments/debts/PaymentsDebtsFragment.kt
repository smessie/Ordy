package com.ordy.app.ui.payments.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.databinding.FragmentPaymentsDebtsBinding
import com.ordy.app.ui.payments.PaymentsViewModel

class PaymentsDebtsFragment : Fragment() {
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

        inflater.inflate(R.layout.fragment_payments_debts, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsDebtsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsDebtsHandlers(this, viewModel)

        return binding.root
    }
}