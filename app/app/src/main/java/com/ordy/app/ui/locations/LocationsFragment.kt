package com.ordy.app.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.FragmentLocationsBinding

class LocationsFragment : Fragment() {

    private val viewModel: LocationsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    private lateinit var listAdapter: LocationsListAdapter

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
        binding.handlers = LocationsBindings(this, viewModel)
        binding.viewmodel = viewModel

        // Setup the list view
        val listView: ListView = binding.root.findViewById(R.id.locations)
        val listViewEmpty: LinearLayout = binding.root.findViewById(R.id.locations_empty)

        // Create the list view adapter
        listAdapter = LocationsListAdapter(
            requireContext(),
            viewModel
        )

        listView.apply {
            adapter = listAdapter
            emptyView = listViewEmpty
        }

        val searchLoading: ProgressBar =
            binding.root.findViewById(R.id.locations_search_loading)

        // Watch changes to the the "search value"
        viewModel.searchValueData.observe(viewLifecycleOwner, Observer {

            // Update the locations
            viewModel.updateLocations()
        })

        // Watch changes to the "locations"
        viewModel.getLocationsMLD().observe(viewLifecycleOwner, Observer {

            // Show a loading indicator in the searchbox.
            // Hide the list view while loading.
            when (it.status) {
                QueryStatus.LOADING -> {
                    searchLoading.visibility = View.VISIBLE
                    listView.emptyView = null
                }

                QueryStatus.SUCCESS -> {
                    searchLoading.visibility = View.INVISIBLE
                    listView.emptyView = listViewEmpty
                }

                QueryStatus.ERROR -> {
                    searchLoading.visibility = View.INVISIBLE

                    ErrorHandler.handle(it.error, view)
                }

                else -> {
                }
            }

            // Update the list adapter
            listAdapter.notifyDataSetChanged()
        })

        return binding.root
    }
}