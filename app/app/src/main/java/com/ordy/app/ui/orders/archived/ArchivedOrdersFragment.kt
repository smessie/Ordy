package com.ordy.app.ui.orders.archived

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
import com.ordy.app.databinding.FragmentOrdersArchivedBinding
import com.ordy.app.ui.orders.overview.users.OrdersListAdapter
import com.ordy.app.ui.orders.OrdersViewModel

class ArchivedOrdersFragment : Fragment() {

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

        // Create binding for the fragment.
        val binding = FragmentOrdersArchivedBinding.inflate(inflater, container, false)
        binding.handlers = ArchivedOrdersHandlers(this, viewModel)

        // Create the list view adapter
        listAdapter = OrdersListAdapter(
            requireContext(),
            Query()
        )
        binding.root.findViewById<ListView>(R.id.orders_archived).adapter = listAdapter

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