package com.ordy.app.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentLocationsBinding
import kotlinx.android.synthetic.main.fragment_locations.view.*

class LocationsFragment : Fragment() {

    private val viewModel: LocationsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    private lateinit var baseAdapter: LocationsBaseAdapter

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_locations, container, false)

        // Create binding for the fragment.
        val binding = FragmentLocationsBinding.inflate(inflater, container, false)
        binding.handlers = LocationsHandlers(this, viewModel)
        binding.viewmodel = viewModel

        // Setup the list view
        val listView: ListView = binding.root.locations
        val listViewEmpty: LinearLayout = binding.root.locations_empty

        // Create the list view adapter
        baseAdapter = LocationsBaseAdapter(
            requireContext(),
            viewModel,
            viewLifecycleOwner,
            binding.root
        )

        listView.apply {
            adapter = baseAdapter
            emptyView = listViewEmpty
        }

        // Swipe to refresh
        binding.locationsRefresh.setOnRefreshListener {
            viewModel.updateLocations()
        }

        viewModel.getLocationsMLD().observe(viewLifecycleOwner, Observer {

            // Stop refreshing when query is loaded
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.locationsRefresh.isRefreshing = false
            }

            baseAdapter.notifyDataSetChanged()
        })

        // Watch changes to the the "search value"
        viewModel.searchValueData.observe(viewLifecycleOwner, Observer {

            // Update the locations
            viewModel.updateLocations()
        })

        return binding.root
    }
}