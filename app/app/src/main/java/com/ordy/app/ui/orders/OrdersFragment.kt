package com.ordy.app.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.active.ActiveOrdersFragment
import com.ordy.app.ui.orders.archived.ArchivedOrdersFragment
import com.ordy.app.util.TabsAdapter
import com.ordy.app.util.TabsEntry
import kotlinx.android.synthetic.main.fragment_orders.view.*

class OrdersFragment : Fragment() {

    private lateinit var tabsAdapter: TabsAdapter

    private val viewModel: OrdersViewModel by activityViewModels { ApiServiceViewModelFactory(requireContext()) }

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

        // Swipe to refresh
        view.orders_refresh.setOnRefreshListener {
            viewModel.refreshOrders()
        }

        // Stop refreshing on load
        viewModel.orders.observe(viewLifecycleOwner, Observer {
            if(it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                view.orders_refresh.isRefreshing = false
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
        val viewPager: ViewPager = view.findViewById(R.id.tabs_view)
        viewPager.adapter = tabsAdapter

        // Link the viewpager to the tablayout.
        val tabs: TabLayout = view.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}