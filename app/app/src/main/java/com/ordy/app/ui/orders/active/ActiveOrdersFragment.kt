package com.ordy.app.ui.orders.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.Query
import com.ordy.app.databinding.FragmentOrdersActiveBinding
import com.ordy.app.ui.orders.OrdersListAdapter
import com.ordy.app.ui.orders.OrdersViewModel
import java.lang.IllegalStateException

class ActiveOrdersFragment : Fragment() {

    private val viewModel: OrdersViewModel by viewModels { ApiServiceViewModelFactory(requireContext()) }

    private var listAdapter: OrdersListAdapter? = null

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
            requireContext(),
            Query()
        )
        binding.root.findViewById<ListView>(R.id.orders_active).adapter = listAdapter

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Update the list adapter when the "orders" query updates
        viewModel.activeOrders.observe(this, Observer {

            val listAdapter = this.listAdapter ?: throw IllegalStateException("List adapter should not be null")

            // Update the orders
            listAdapter.orders = it

            // Notify the changes to the list view (to re-render automatically)
            listAdapter.notifyDataSetChanged()
        })
    }
}