package com.ordy.app.ui.groups.overview

import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityOverviewGroupBinding
import kotlinx.android.synthetic.main.activity_overview_group.*

class OverviewGroupActivity : AppCompatActivity() {

    private val viewModel: OverviewGroupViewModel by viewModels {
        ApiServiceViewModelFactory(
            applicationContext
        )
    }

    private var listAdapter: OverviewGroupListAdapter? = null
    lateinit var handlers: OverviewGroupHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewGroupBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_overview_group)
        handlers = OverviewGroupHandlers(this, viewModel)
        binding.handlers = handlers
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.rootView = binding.root

        // Extract the "group_id" from the given intent variables.
        val groupId = intent.getIntExtra("group_id", -1)

        // Fetch the specific group.
        FetchHandler.handle(viewModel.group, viewModel.apiService.group(groupId))

        // Create the list view adapter
        listAdapter = OverviewGroupListAdapter(applicationContext, viewModel, handlers)
        binding.root.findViewById<ListView>(R.id.group_members).adapter = listAdapter

        // Set the action bar elevation to 0, since the group extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        // Observe the changes of the fetch.
        viewModel.group.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    val group = it.requireData()

                    group_title.text = group.name
                    group_members_amount.text = group.members.size.toString()

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

        // Observe the changes of the remove member request.
        viewModel.removeResult.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    viewModel.handlingRemoveRequest = false
                    // Update the specific group.
                    FetchHandler.handle(viewModel.group, viewModel.apiService.group(groupId))
                }

                QueryStatus.ERROR -> {
                    viewModel.handlingRemoveRequest = false
                    if (viewModel.rootView != null) {
                        ErrorHandler.handleRawGeneral(
                            "Remove member request failed. Please try again...",
                            viewModel.rootView!!
                        )
                    }
                }
            }
        })

        // Observe the changes of the leave group request.
        viewModel.leaveResult.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    // Go back to the GroupsFragment
                    finish()
                }

                QueryStatus.ERROR -> {
                    if (viewModel.rootView != null) {
                        ErrorHandler.handleRawGeneral(
                            "Leaving group failed. Please try again...",
                            viewModel.rootView!!
                        )
                    }
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
}
