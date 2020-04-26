package com.ordy.app.ui.payments.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentPaymentsDebtsBinding
import com.ordy.app.ui.payments.PaymentsListAdapter
import com.ordy.app.ui.payments.PaymentsType
import com.ordy.app.ui.payments.PaymentsViewModel
import kotlinx.android.synthetic.main.fragment_payments_debts.view.*

class PaymentsDebtsFragment : Fragment() {

    private val viewModel: PaymentsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    private lateinit var listAdapter: PaymentsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        inflater.inflate(R.layout.fragment_payments_debts, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsDebtsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsDebtsHandlers(this, viewModel)

        // Initialize listViewAdapter
        listAdapter = PaymentsListAdapter(requireContext(), viewModel, PaymentsType.Debts)

        binding.root.findViewById<ListView>(R.id.payments_debts).apply {
            adapter = listAdapter
            emptyView = binding.root.findViewById(R.id.payments_debts_empty)
        }

        viewModel.refreshDebts()

        // Swipe to refresh
        binding.root.payments_debts_refresh.setOnRefreshListener {
            viewModel.refreshDebts()
        }

        // Observe the debts
        viewModel.getDebtsMLD().observe(viewLifecycleOwner, Observer {
            // Stop refreshing when loaded
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.root.payments_debts_refresh.isRefreshing = false
            }
        })

        return binding.root
    }
}