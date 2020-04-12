package com.ordy.app.ui.orders.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentOrdersActiveBinding
import com.ordy.app.ui.orders.OrdersListAdapter
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.ui.orders.OrdersViewModel
import kotlinx.android.synthetic.main.fragment_orders_active.view.*

class ActiveOrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    private lateinit var listAdapter: OrdersListAdapter

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_orders_active, container, false)

        // Create binding for the fragment.
        val binding = FragmentOrdersActiveBinding.inflate(inflater, container, false)
        binding.handlers = ActiveOrdersHandlers(this, viewModel)

        // Create the list view adapter
        listAdapter = OrdersListAdapter(
            activity as AppCompatActivity,
            requireContext(),
            viewModel,
            OrdersStatus.ACTIVE
        )

        binding.root.findViewById<ListView>(R.id.orders_active).apply {
            adapter = listAdapter
            emptyView = binding.root.findViewById(R.id.orders_active_empty)
        }

        viewModel.refreshOrders()

        // Swipe to refresh
        binding.root.orders_active_refresh.setOnRefreshListener {
            viewModel.refreshOrders()
        }

        // Observe the orders
        viewModel.getOrdersMLD().observe(viewLifecycleOwner, Observer {

            // Stop refreshing on load
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.root.orders_active_refresh.isRefreshing = false
            }

        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Update the list adapter when the "orders" query updates
        viewModel.getOrdersMLD().observe(this, Observer {

            // Notify the changes to the list view (to re-render automatically)
            listAdapter.update()
        })
    }
}