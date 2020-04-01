package com.ordy.app.ui.groups.invite

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityInviteMemberBinding
import kotlinx.android.synthetic.main.activity_invite_member.*

class InviteMemberActivity : AppCompatActivity() {

    private val viewModel: InviteMemberViewModel by viewModels {
        ApiServiceViewModelFactory(
            applicationContext
        )
    }

    private var listAdapter: InviteMemberListAdapter? = null
    lateinit var handlers: InviteMemberHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityInviteMemberBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_invite_member)
        handlers = InviteMemberHandlers(this, viewModel)
        binding.handlers = handlers

        viewModel.rootView = binding.root

        // Extract the "group_id" from the given intent variables.
        viewModel.groupId = intent.getIntExtra("group_id", -1)

        // Create the list view adapter
        listAdapter = InviteMemberListAdapter(applicationContext, viewModel, handlers)
        binding.root.findViewById<ListView>(R.id.users).apply {
            adapter = listAdapter
            emptyView = binding.root.findViewById(R.id.users_empty)
        }

        // Set the action bar elevation to 0, since the invite screen extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        viewModel.users.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    val listAdapter = this.listAdapter
                        ?: throw IllegalStateException("List adapter should not be null")

                    // Notify the changes to the list view (to re-render automatically)
                    listAdapter.notifyDataSetChanged()
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(it.error, binding.root, emptyList())
                }
            }
        })

        viewModel.inviteResult.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    viewModel.handlingInviteRequest = false
                }

                QueryStatus.ERROR -> {
                    viewModel.handlingInviteRequest = false

                    ErrorHandler.handle(
                        it.error, binding.root, listOf(
                            InputField("username", this.input_username)
                        )
                    )
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Add search menu to appbar.
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            // Make the top back button go back to GroupsFragment.
            finish()
            true
        }

        R.id.search -> {
            // Call the function to save new group when button clicked.
            handlers.onSearchButtonClick()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
