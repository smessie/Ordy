package com.ordy.app.ui.groups.invite

import android.view.View
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.activity_invite_member.*

class InviteMemberHandlers(
    val activity: InviteMemberActivity,
    val viewModel: InviteMemberViewModel,
    val view: View,
    val groupId: Int
) {

    /**
     * Handle the invite button clicked
     */
    fun onInviteButtonClick(userId: Int) {
        if (viewModel.inviteResult.value?.status != QueryStatus.LOADING) {
            FetchHandler.handle(
                viewModel.inviteResult,
                viewModel.apiService.createInviteGroup(groupId, userId)
            )
        } else {
            ErrorHandler.handleRawGeneral(
                "Calm down ;) another request is still processing...",
                view
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
            viewModel.apiService.searchMatchingInviteUsers(groupId, username)
        )
    }
}