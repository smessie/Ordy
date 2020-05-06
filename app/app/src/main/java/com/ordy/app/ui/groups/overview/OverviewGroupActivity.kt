package com.ordy.app.ui.groups.overview

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewGroupBinding
import kotlinx.android.synthetic.main.activity_overview_group.*
import kotlinx.android.synthetic.main.activity_overview_group.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class OverviewGroupActivity : AppCompatActivity() {

    private val viewModel: OverviewGroupViewModel by viewModel()

    private lateinit var baseAdapter: OverviewGroupBaseAdapter
    lateinit var handlers: OverviewGroupHandlers
    private var groupId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewGroupBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_overview_group)
        handlers = OverviewGroupHandlers(this, viewModel, binding.root)
        binding.handlers = handlers
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Extract the "group_id" from the given intent variables.
        groupId = intent.getIntExtra("group_id", -1)

        // Fetch the specific group.
        viewModel.refreshGroup(groupId)

        // Swipe to refresh
        binding.root.group_refresh.setOnRefreshListener {
            viewModel.refreshGroup(groupId)
        }

        // Create the list view adapter
        baseAdapter = OverviewGroupBaseAdapter(applicationContext, viewModel, handlers, this, binding.root)
        binding.root.group_members.adapter = baseAdapter

        // Set the action bar elevation to 0, since the group extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        // Observe the changes of the fetch.
        viewModel.groupMLD.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    // Stop the refreshing on load
                    binding.root.group_refresh.isRefreshing = false

                    val group = it.requireData()

                    group_title.text = group.name
                    group_members_amount.text = group.members?.size.toString()
                }

                QueryStatus.ERROR -> {
                    // Stop the refreshing on load
                    binding.root.group_refresh.isRefreshing = false

                    ErrorHandler().handle(it.error, binding.root, emptyList())
                }

                else -> {
                }
            }
        })

        // Observe the changes of the remove member request.
        viewModel.removeMemberMLD.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    // Update the specific group.
                    viewModel.refreshGroup(groupId)
                }

                QueryStatus.ERROR -> {
                    ErrorHandler().handle(it.error, binding.root, listOf())
                }

                else -> {
                }
            }
        })

        // Observe the changes of the leave group request.
        viewModel.leaveGroupMLD.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    // Go back to the GroupsFragment
                    finish()
                }

                QueryStatus.ERROR -> {
                    ErrorHandler().handle(it.error, binding.root, listOf())
                }

                else -> {
                }
            }
        })

    }

    /**
     * Make the top back button go back to GroupsFragment
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        // Update the group.
        viewModel.refreshGroup(groupId)
    }
}
