package com.ordy.app.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.activity_profile.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {

    private val viewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binding for the activity.
        val binding: ActivityProfileBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_profile)
        val handlers = ProfileHandlers(this, viewModel, binding.root)
        binding.handlers = handlers

        // Load user information
        viewModel.refreshUserInfo()

        viewModel.getUserMLD().observe(this, Observer {
            when (it.status) {

                QueryStatus.SUCCESS -> {
                    val user = it.requireData()

                    binding.root.user_info_username.text = user.username
                    binding.root.user_info_email.text = user.email
                }

                QueryStatus.ERROR -> {
                    ErrorHandler().handle(it.error, binding.root)
                }

                else -> {
                    binding.root.user_info_username.text = getString(R.string.loading)
                    binding.root.user_info_email.text = ""
                }
            }
        })

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
