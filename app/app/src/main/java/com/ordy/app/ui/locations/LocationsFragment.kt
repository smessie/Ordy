package com.ordy.app.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.databinding.FragmentLocationsBinding

class LocationsFragment : Fragment() {

    private val viewModel: LocationsViewModel by viewModels { ApiServiceViewModelFactory(requireContext()) }

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

        return binding.root
    }
}