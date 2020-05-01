package com.ordy.app.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.databinding.FragmentLocationsBinding
import kotlinx.android.synthetic.main.fragment_locations.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LocationsFragment : Fragment() {

    private val viewModel: LocationsViewModel by sharedViewModel()

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
            this,
            binding.root
        )

        listView.apply {
            adapter = baseAdapter
            emptyView = listViewEmpty
        }

        // Watch changes to the the "search value"
        viewModel.searchValueData.observe(viewLifecycleOwner, Observer {

            // Update the locations
            viewModel.updateLocations()
        })

        return binding.root
    }
}