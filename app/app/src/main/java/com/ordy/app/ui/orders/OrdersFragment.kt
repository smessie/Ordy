package com.ordy.app.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.active.ActiveOrdersFragment
import com.ordy.app.ui.orders.archived.ArchivedOrdersFragment
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.types.TabsEntry
import kotlinx.android.synthetic.main.fragment_orders.view.*

class OrdersFragment : Fragment() {

    private lateinit var tabsAdapter: TabsAdapter

    private val viewModel: OrdersViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        // Create the tabs adapter.
        tabsAdapter = TabsAdapter(childFragmentManager)
        tabsAdapter.addTabsEntry(TabsEntry(ActiveOrdersFragment(), "Active orders"))
        tabsAdapter.addTabsEntry(TabsEntry(ArchivedOrdersFragment(), "Archived orders"))

        // Observe the orders
        viewModel.getOrdersMLD().observe(viewLifecycleOwner, Observer {

            // Show an error when necessary
            if (it.status == QueryStatus.ERROR) {
                ErrorHandler().handle(it.error, view)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Setup the tabsbar.
         */
        // Link the adapter to the viewpager.
        val viewPager = view.tabs_view
        viewPager.adapter = tabsAdapter

        // Link the viewpager to the tablayout.
        val tabs: TabLayout = view.tabs
        tabs.setupWithViewPager(viewPager)
    }
}