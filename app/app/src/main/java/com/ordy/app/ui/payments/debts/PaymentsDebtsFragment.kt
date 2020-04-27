package com.ordy.app.ui.payments.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentPaymentsDebtsBinding
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsListAdapter
import com.ordy.app.ui.payments.PaymentsViewModel

class PaymentsDebtsFragment(
    private val parentFragment: PaymentsFragment
) : Fragment() {

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
        binding.viewModel = viewModel

        // Initialize listViewAdapter
        listAdapter = PaymentsDebtsListAdapter(
            requireContext(),
            viewModel,
            parentFragment
        )

        binding.paymentsDebts.apply {
            adapter = listAdapter
            emptyView = binding.paymentsDebtsEmpty
        }

        viewModel.refreshDebts()

        // Swipe to refresh
        binding.paymentsDebtsRefresh.setOnRefreshListener {
            viewModel.refreshDebts()
        }

        // Observe the input field
        viewModel.debtsSearch.observe(viewLifecycleOwner, Observer {
            listAdapter.update()
        })

        // Observe the debts
        viewModel.getDebtsMLD().observe(viewLifecycleOwner, Observer {
            // Stop refreshing when loaded
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.paymentsDebtsRefresh.isRefreshing = false
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Update the list adapter when the "orders" query updates
        viewModel.getDebtsMLD().observe(this, Observer {

            // Notify the changes to the list view (to re-render automatically)
            listAdapter.update()
        })
    }
}