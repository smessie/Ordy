package com.ordy.app.ui.profile

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.actions.InviteAction
import com.ordy.app.api.models.actions.enums.InviteActionOptions
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import okhttp3.ResponseBody

class ProfileHandlers(
    val activity: ProfileActivity,
    val viewModel: ProfileViewModel,
    val view: View
) {

    fun handleClickInviteAction(
        action: InviteActionOptions,
        groupId: Int,
        actionInviteResult: MutableLiveData<Query<ResponseBody>>
    ) {
        if (actionInviteResult.value?.status != QueryStatus.LOADING) {
            FetchHandler.handle(
                actionInviteResult, viewModel.apiService.userActionInvites(
                    InviteAction(action), groupId
                )
            )
        }
    }
}