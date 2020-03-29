package com.ordy.app.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.databinding.FragmentGroupsBinding


class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by activityViewModels { ApiServiceViewModelFactory(requireContext()) }

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_groups, container, false)

        // Create binding for the fragment.
        val binding = FragmentGroupsBinding.inflate(inflater, container, false)
        binding.handlers = GroupsHandlers(this, viewModel)

        return binding.root
    }
}