package com.ordy.app.ui.groups

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.create.CreateGroupActivity
import kotlinx.android.synthetic.main.fragment_groups.view.*


class GroupsFragment : Fragment() {

    private val viewModel: GroupsViewModel by activityViewModels {
        RepositoryViewModelFactory(
            requireContext()
        )
    }

    private lateinit var listAdapter: GroupsListAdapter

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
        listAdapter = GroupsListAdapter(requireContext(), viewModel)
        view.groups.apply {
            adapter = listAdapter
            emptyView = emptyList
        }

        // Fetch the list of groups
        viewModel.refreshGroups()

        // Swipe to refresh
        view.groups_refresh.setOnRefreshListener {
            viewModel.refreshGroups()
        }

        // Binding button to load new activity
        val createButton = view.create_group_button
        createButton.setOnClickListener {
            val intent = Intent(context, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        viewModel.getGroupsMLD().observe(viewLifecycleOwner, Observer {
            // Stop refreshing on load
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                view.groups_refresh.isRefreshing = false
            }

            // Notify changes to list view
            listAdapter.notifyDataSetChanged()
        })

        return view
    }

    override fun onResume() {
        super.onResume()

        // Update the groups.
        viewModel.refreshGroups()
    }
}