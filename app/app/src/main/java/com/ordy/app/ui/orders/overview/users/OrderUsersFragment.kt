package com.ordy.app.ui.orders.overview.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentOrderUsersBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import kotlinx.android.synthetic.main.fragment_order_general.view.*
import kotlinx.android.synthetic.main.fragment_order_users.view.*
import kotlinx.android.synthetic.main.fragment_order_users.view.order_items
import kotlinx.android.synthetic.main.fragment_order_users.view.order_items_empty
import kotlinx.android.synthetic.main.fragment_order_users.view.order_refresh

class OrderUsersFragment : Fragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    private lateinit var baseAdapter: OrderUsersBaseAdapter

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_order_users, container, false)

        // Create binding for the fragment.
        val binding = FragmentOrderUsersBinding.inflate(inflater, container, false)
        binding.handlers = OrderUsersHandlers(this, viewModel)

        // Create the list view adapter
        baseAdapter = OrderUsersBaseAdapter(requireContext(), viewModel, viewLifecycleOwner)
        binding.root.order_items.apply {
            adapter = baseAdapter
            emptyView = binding.root.order_items_empty
        }

        // Swipe to refresh
        binding.root.order_refresh.setOnRefreshListener {
            viewModel.refreshOrder()
        }

        // Stop refreshing on load
        viewModel.getOrderMLD().observe(viewLifecycleOwner, Observer {
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.root.order_refresh.isRefreshing = false
            }
        })

        return binding.root
    }
}