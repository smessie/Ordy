package com.ordy.app.ui.orders.overview.personal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentOrderPersonalBinding
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.util.OrderUtil
import com.ordy.app.util.TimerUtil
import kotlinx.android.synthetic.main.fragment_order_personal.*
import kotlinx.android.synthetic.main.fragment_order_personal.view.*

class OrderPersonalFragment : Fragment() {

    private val viewModel: OverviewOrderViewModel by activityViewModels()

    private var listAdapter: OrderPersonalListAdapter? = null

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
        binding.root.findViewById<ListView>(R.id.order_items).adapter = listAdapter

        // Update the list adapter when the "order" query updates
        viewModel.order.observe(viewLifecycleOwner, Observer {

            val listAdapter = this.listAdapter ?: throw IllegalStateException("List adapter should not be null")

            listAdapter.update()
        })

        return binding.root
    }
}