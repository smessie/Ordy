package com.ordy.app.ui.groups.overview

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityOverviewGroupBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_overview_group)
        binding.handlers = OverviewGroupHandlers(this, viewModel)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        // Extract the "group_id" from the given intent variables.
        val groupId = intent.getIntExtra("group_id", -1)

        // Fetch the specific order.
        FetchHandler.handle(viewModel.group, viewModel.apiService.group(groupId))

        // Create the list view adapter
        listAdapter = OverviewGroupListAdapter(applicationContext, Query())
        binding.root.findViewById<ListView>(R.id.group_members).adapter = listAdapter

        // Set the action bar elevation to 0, since the group extends the action bar.
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0F
        }

        // Observe the changes of the fetch.
        viewModel.group.observe(this, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    Log.i("TAG", "NOW LOADING")
                }

                QueryStatus.SUCCESS -> {
                    val group = it.requireData()

                    group_title.text = group.name
                    group_members_amount.text = group.members.size.toString()

                    val listAdapter = this.listAdapter
                        ?: throw IllegalStateException("List adapter should not be null")

                    // Update the members by updating the group
                    listAdapter.group = it

                    // Notify the changes to the list view (to re-render automatically)
                    listAdapter.notifyDataSetChanged()
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(it.error, binding.root, emptyList())
                }
            }
        })

    }
}
