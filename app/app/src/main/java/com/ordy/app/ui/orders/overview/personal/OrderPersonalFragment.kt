package com.ordy.app.ui.orders.overview.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.databinding.FragmentOrderPersonalBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import kotlinx.android.synthetic.main.fragment_order_general.view.*

class OrderPersonalFragment : Fragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    private lateinit var listAdapter: OrderPersonalListAdapter

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
        listAdapter = OrderPersonalListAdapter(
            requireContext(),
            binding.root,
            handlers,
            this,
            viewModel
        )
        binding.root.order_items.apply {
            adapter = listAdapter
            emptyView = binding.root.order_items_empty
        }

        // Update the list adapter when the "order" query updates
        viewModel.getOrderMLD().observe(viewLifecycleOwner, Observer {
            listAdapter.update()
        })

        return binding.root
    }
}