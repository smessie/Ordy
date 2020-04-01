package com.ordy.app.ui.groups.invite

import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.activity_invite_member.*

class InviteMemberHandlers(
    val activity: InviteMemberActivity,
    val viewModel: InviteMemberViewModel
) {

    /**
     * Handle the invite button clicked
     */
    fun onInviteButtonClick(userId: Int) {
        if (!viewModel.handlingInviteRequest) {
            viewModel.handlingInviteRequest = true

            FetchHandler.handle(
                viewModel.inviteResult,
                viewModel.apiService.createInviteGroup(viewModel.groupId, userId)
            )
        } else {
            ErrorHandler.handleRawGeneral(
                "Calm down ;) another request is still processing...",
                viewModel.rootView
            )
        }
    }

    /**
     * Handle the search button clicked
     */
    fun onSearchButtonClick() {
        val username = InputUtil.extractText(activity.input_username)

        FetchHandler.handle(
            viewModel.users,
            viewModel.apiService.searchMatchingInviteUsers(viewModel.groupId, username)
        )
    }
}