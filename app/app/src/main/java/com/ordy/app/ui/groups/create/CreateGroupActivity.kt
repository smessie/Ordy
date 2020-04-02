package com.ordy.app.ui.groups.create

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.ApiServiceViewModelFactory
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityCreateGroupBinding
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupActivity : AppCompatActivity() {

    private val viewModel: CreateGroupViewModel by viewModels {
        ApiServiceViewModelFactory(
            applicationContext
        )
    }

    lateinit var handlers: CreateGroupHandlers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityCreateGroupBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_group)
        handlers = CreateGroupHandlers(this, viewModel, binding.root)
        binding.lifecycleOwner = this
        binding.handlers = handlers
        binding.viewModel = viewModel

        viewModel.createResult.observe(this, Observer {

            when (it.status) {

                QueryStatus.SUCCESS -> {
                    // Go to newly created group
                    val intent = Intent(this, OverviewGroupActivity::class.java)
                    // Pass the group id as extra information
                    intent.putExtra("group_id", it.requireData().id)
                    finish()
                    startActivity(intent)
                }

                QueryStatus.ERROR -> {
                    ErrorHandler.handle(
                        it.error, binding.root, listOf(
                            InputField("name", this.input_name)
                        )
                    )
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Add create menu to appbar.
        menuInflater.inflate(R.menu.create_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        android.R.id.home -> {
            // Make the top back button go back to GroupsFragment.
            finish()
            true
        }

        R.id.create -> {
            // Call the function to save new group when button clicked.
            handlers.onCreateButtonClick()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
