package com.ordy.app.ui.groups.overview

import android.content.Intent
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.ui.groups.create.CreateGroupActivity

class OverviewGroupHandlers(
    val activity: OverviewGroupActivity,
    val viewModel: OverviewGroupViewModel
) {
    /**
     * Handle the leave button clicked
     */
    fun onLeaveButtonClick() {
        if (viewModel.group.value != null) {
            FetchHandler.handle(
                viewModel.leaveResult,
                viewModel.apiService.userLeaveGroup(viewModel.group.value!!.requireData().id)
            )
        } else {
            ErrorHandler.handleRawGeneral(
                "You already are no member of this group.",
                viewModel.rootView
            )
        }
    }

    /**
     * Handle the invite button clicked
     */
    fun onInviteButtonClick() {
        if (viewModel.group.value != null) {
            // TODO: change activity to InviteMemberActivity when created
            val intent = Intent(viewModel.rootView.context, CreateGroupActivity::class.java)

            // Pass the group as extra information
            intent.putExtra("group_id", viewModel.group.value!!.requireData().id)

            viewModel.rootView.context.startActivity(intent)
        } else {
            ErrorHandler.handleRawGeneral(
                "Request failed. Please try again...",
                viewModel.rootView
            )
        }
    }

    fun removeMember(groupId: Int, userId: Int) {
        if (!viewModel.handlingRemoveRequest) {
            viewModel.handlingRemoveRequest = true
            FetchHandler.handle(
                viewModel.removeResult, viewModel.apiService.deleteMemberGroup(groupId, userId)
            )
        } else {
            ErrorHandler.handleRawGeneral(
                "Calm down ;) another request is still processing...",
                viewModel.rootView
            )
        }
    }
}