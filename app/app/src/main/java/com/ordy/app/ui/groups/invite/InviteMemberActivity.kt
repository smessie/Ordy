package com.ordy.app.ui.groups.invite

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.RepositoryViewModelFactory
import com.ordy.app.databinding.ActivityInviteMemberBinding
import kotlinx.android.synthetic.main.activity_invite_member.view.*

class InviteMemberActivity : AppCompatActivity() {

    private val viewModel: InviteMemberViewModel by viewModels {
        RepositoryViewModelFactory(
            applicationContext
        )
    }

    lateinit var baseAdapter: InviteMemberBaseAdapter
    lateinit var handlers: InviteMemberHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract the "group_id" from the given intent variables.
        val groupId = intent.getIntExtra("group_id", -1)

        // Create binding for the activity.
        val binding: ActivityInviteMemberBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_invite_member)
        handlers = InviteMemberHandlers(this, viewModel, binding.root, groupId)
        binding.handlers = handlers
        binding.viewModel = viewModel

        val listView = binding.root.users

        // Create the list view adapter
        baseAdapter =
            InviteMemberBaseAdapter(applicationContext, this, viewModel, handlers, binding.root)
        listView.apply {
            adapter = baseAdapter
            emptyView = binding.root.users_empty
        }

        // Set the action bar elevation to 0, since the invite screen extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        // Watch changes to the the "search value"
        viewModel.getSearchValueData().observe(this, Observer {

            // Update the users
            viewModel.updateUsers(groupId)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            // Make the top back button go back to GroupsFragment.
            finish()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
