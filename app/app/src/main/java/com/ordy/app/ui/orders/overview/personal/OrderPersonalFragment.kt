package com.ordy.app.ui.orders.overview.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentOrderPersonalBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.util.OrderUtil
import kotlinx.android.synthetic.main.fragment_order_general.view.*
import kotlinx.android.synthetic.main.fragment_order_personal.*

class OrderPersonalFragment : Fragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    private lateinit var baseAdapter: OrderPersonalBaseAdapter

    lateinit var handlers: OrderPersonalHandlers

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_order_personal, container, false)

        // Create binding for the fragment.
        val binding = FragmentOrderPersonalBinding.inflate(inflater, container, false)
        handlers = OrderPersonalHandlers(this, viewModel)
        binding.handlers = handlers

        // Create the list view adapter
        baseAdapter = OrderPersonalBaseAdapter(
            requireContext(),
            binding.root,
            handlers,
            this,
            viewModel,
            viewLifecycleOwner
        )
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


    override fun onDestroy() {
        super.onDestroy()

        // Destroy the adapter & stop all the ongoing timer tasks.
        baseAdapter.destroy()
    }
}