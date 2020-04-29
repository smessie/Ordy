package com.ordy.app.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.activity_profile.view.*

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModels {
        RepositoryViewModelFactory(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityProfileBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val handlers = ProfileHandlers(this, viewModel, binding.root)
        binding.handlers = handlers

        viewModel.refreshInvites()

        // Swipe to refresh
        binding.root.group_invites_refresh.setOnRefreshListener {
            viewModel.refreshInvites()
        }

        val listAdapter =
            InvitesBaseAdapter(applicationContext, viewModel, this, handlers, binding.root)
        binding.root.group_invites.apply {
            adapter = listAdapter
            emptyView = binding.root.group_invites_empty
        }

        // Set the action bar elevation to 0, since the group extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }
    }

    override fun onResume() {
        super.onResume()

        // Update the invites.
        viewModel.refreshInvites()
    }
}
