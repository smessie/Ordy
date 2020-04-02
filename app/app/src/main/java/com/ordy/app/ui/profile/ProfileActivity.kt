package com.ordy.app.ui.profile

import android.os.Bundle
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.activity_profile.view.*

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModels {
        ApiServiceViewModelFactory(
            applicationContext
        )
    }

    private var listAdapter: InvitesListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityProfileBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val handlers = ProfileHandlers(this, viewModel, binding.root)
        binding.handlers = handlers

        // Swipe to refresh
        binding.root.group_invites_refresh.setOnRefreshListener {
            viewModel.refreshInvites()
        }

        val listAdapter = InvitesListAdapter(applicationContext, viewModel, this, handlers)
        binding.root.findViewById<ListView>(R.id.group_invites).apply {
            adapter = listAdapter
            emptyView = binding.root.findViewById(R.id.group_invites_empty)
        }

        // Set the action bar elevation to 0, since the group extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        viewModel.invites.observe(this, Observer {
            // Stop refreshing on load
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                binding.root.group_invites_refresh.isRefreshing = false
            }

            // Notify changes to list view
            listAdapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()

        // Update the invites.
        viewModel.refreshInvites()
    }
}
