package com.ordy.app.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.Query
import com.ordy.app.databinding.FragmentGroupsBinding
import java.lang.IllegalStateException


class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by viewModels { ApiServiceViewModelFactory(requireContext()) }

    private var listAdapter: GroupsListAdapter? = null

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

        // list view adapter
        listAdapter = GroupsListAdapter(requireContext(), Query())
        binding.root.findViewById<ListView>(R.id.groups).adapter = listAdapter

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.groups.observe(this, Observer {
            val listAdapter = this.listAdapter ?: throw IllegalStateException("List adapter should not be null!")

            listAdapter.groups = it

            // notify changes to list view
            listAdapter.notifyDataSetChanged()
        })
    }
}