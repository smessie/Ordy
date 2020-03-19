package com.ordy.app.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ordy.app.R
import com.ordy.app.ui.groups.GroupsViewModel
import com.ordy.app.ui.payments.PaymentsViewModel

class LocationsFragment : Fragment() {

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_locations, container, false)

        // Create the view model.
        val viewModel: LocationsViewModel by viewModels()

        return view
    }
}