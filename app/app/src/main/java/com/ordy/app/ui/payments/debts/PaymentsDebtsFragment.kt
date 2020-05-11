package com.ordy.app.ui.payments.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentPaymentsDebtsBinding
import com.ordy.app.ui.payments.PaymentsBaseAdapter
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PaymentsDebtsFragment : Fragment() {

    private val viewModel: PaymentsViewModel by sharedViewModel()

    private lateinit var baseAdapter: PaymentsBaseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflater.inflate(R.layout.fragment_payments_debts, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsDebtsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsDebtsHandlers(this, viewModel)
        binding.viewModel = viewModel

        // Initialize listViewAdapter
        baseAdapter = PaymentsDebtsBaseAdapter(
            requireContext(),
            viewModel,
            requireParentFragment() as PaymentsFragment,
            viewLifecycleOwner
        )

        binding.paymentsDebts.apply {
            adapter = baseAdapter
            emptyView = binding.paymentsDebtsEmpty
        }

        viewModel.refreshDebts()

        // Swipe to refresh
        binding.paymentsDebtsRefresh.setOnRefreshListener {
            viewModel.refreshDebts()
        }

        // Observe the debts
        viewModel.getDebtsMLD().observe(viewLifecycleOwner, Observer {
            // Stop refreshing when loaded
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.paymentsDebtsRefresh.isRefreshing = false
            }
        })

        return binding.root
    }
}