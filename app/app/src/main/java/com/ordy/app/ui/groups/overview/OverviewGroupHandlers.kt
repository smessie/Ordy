package com.ordy.app.ui.groups.overview

import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.invite.InviteMemberActivity
import com.ordy.app.ui.groups.overview.changeName.ChangeGroupNameDialog

class OverviewGroupHandlers(
    val activity: OverviewGroupActivity,
    val viewModel: OverviewGroupViewModel,
    val view: View
) {
    /**
     * Handle the leave button clicked
     */
    fun onLeaveButtonClick() {
        if (viewModel.getGroupMLD().value != null) {
            AlertDialog.Builder(activity).apply {
                setTitle("Are you sure?")
                setMessage("You are about to leave this group")

                setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.userLeaveGroup(viewModel.getGroup().requireData().id)
                }

                setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
        } else {
            ErrorHandler().handleRawGeneral(
                "You already are no member of this group.",
                view
            )
        }
    }

    /**
     * Handle the invite button clicked
     */
    fun onInviteButtonClick() {
        if (viewModel.getGroupMLD().value != null) {
            val intent = Intent(activity, InviteMemberActivity::class.java)

            // Pass the group as extra information
            intent.putExtra("group_id", viewModel.getGroup().requireData().id)

            activity.startActivity(intent)
        } else {
            ErrorHandler().handleRawGeneral(
                "Request failed. Please try again...",
                view
            )
        }
    }

    /**
     * Handle the rename button clicked
     */
    fun onRenameButtonClick() {
        if (viewModel.getGroupMLD().value != null) {
            val manager = this.activity.supportFragmentManager

            val dialog = ChangeGroupNameDialog(
                viewModel = viewModel,
                activityView = view
            )
            dialog.show(manager, activity.getString(R.string.group_rename_dialog_tag))
            viewModel.getRenameGroupMLD().observe(this.activity, Observer {
                // Refresh when query is successful
                when (it.status) {
                    QueryStatus.SUCCESS -> {
                        viewModel.refreshGroup(viewModel.getGroup().requireData().id)
                    }

                    QueryStatus.ERROR -> {
                        ErrorHandler().handle(it.error, view)
                    }

                    else -> {
                        // Do nothing
                    }
                }
            })
        }
    }

    fun removeMember(groupId: Int, userId: Int) {
        if (viewModel.getRemoveMemberMLD().value?.status != QueryStatus.LOADING) {
            viewModel.removeMemberFromGroup(userId, groupId)
        }
    }
}