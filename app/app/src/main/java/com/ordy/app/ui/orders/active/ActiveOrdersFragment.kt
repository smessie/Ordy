package com.ordy.app.ui.orders.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentOrdersActiveBinding
import com.ordy.app.ui.orders.OrdersBaseAdapter
import com.ordy.app.ui.orders.OrdersStatus
import com.ordy.app.ui.orders.OrdersViewModel
import kotlinx.android.synthetic.main.fragment_orders_active.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ActiveOrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by sharedViewModel()

    private lateinit var baseAdapter: OrdersBaseAdapter

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
        baseAdapter = OrdersBaseAdapter(
            activity as AppCompatActivity,
            requireContext(),
            viewModel,
            OrdersStatus.ACTIVE
        )

        binding.root.orders_active.apply {
            adapter = baseAdapter
            emptyView = binding.root.orders_active_empty
        }

        viewModel.refreshOrders(requireContext(), activity)

        // Swipe to refresh
        binding.root.orders_active_refresh.setOnRefreshListener {
            viewModel.refreshOrders(requireContext(), activity)
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

    override fun onDestroy() {
        super.onDestroy()

        // Destroy the adapter & stop all the ongoing timer tasks.
        baseAdapter.destroy()
    }
}