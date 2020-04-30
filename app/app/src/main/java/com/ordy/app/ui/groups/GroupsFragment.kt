package com.ordy.app.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ordy.app.R
import kotlinx.android.synthetic.main.fragment_groups.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by sharedViewModel()

    private lateinit var baseAdapter: GroupsBaseAdapter

    /**
     * Called when view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val emptyList = view.groups_empty

        // list view adapter
        baseAdapter = GroupsBaseAdapter(requireContext(), viewModel, viewLifecycleOwner, view)
        view.groups.apply {
            adapter = baseAdapter
            emptyView = emptyList
        }

        // Fetch the list of groups
        viewModel.refreshGroups()

        // Swipe to refresh
        view.groups_refresh.setOnRefreshListener {
            viewModel.refreshGroups()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        // Update the groups.
        viewModel.refreshGroups()
    }
}