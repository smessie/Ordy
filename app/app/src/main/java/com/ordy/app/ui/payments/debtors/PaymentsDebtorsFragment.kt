package com.ordy.app.ui.payments.debtors

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
import com.ordy.app.databinding.FragmentPaymentsDebtorsBinding
import com.ordy.app.ui.payments.PaymentsFragment
import com.ordy.app.ui.payments.PaymentsListAdapter
import com.ordy.app.ui.payments.PaymentsViewModel

class PaymentsDebtorsFragment(
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
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_payments_debtors, container, false)

        // Create binding for the fragment.
        val binding = FragmentPaymentsDebtorsBinding.inflate(inflater, container, false)
        binding.handlers = PaymentsDebtorsHandlers(this, viewModel)
        binding.viewModel = viewModel

        // Initialize listViewAdapter
        listAdapter = PaymentsDebtorsListAdapter(
            requireContext(),
            viewModel,
            parentFragment
        )

        binding.paymentsDebtors.apply {
            adapter = listAdapter
            emptyView = binding.paymentsDebtorsEmpty
        }

        viewModel.refreshDebtors()

        // Swipe to refresh
        binding.paymentsDebtorsRefresh.setOnRefreshListener {
            viewModel.refreshDebtors()
        }

        // Observe the input field
        viewModel.debtorsSearch.observe(viewLifecycleOwner, Observer {
            listAdapter.update()
        })


        // Observe the debtors
        viewModel.getDebtorsMLD().observe(viewLifecycleOwner, Observer {
            // Stop refreshing when loaded
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.paymentsDebtorsRefresh.isRefreshing = false
            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Update the list adapter when the "orders" query updates
        viewModel.getDebtorsMLD().observe(this, Observer {

            // Notify the changes to the list view (to re-render automatically)
            listAdapter.update()
        })
    }
}