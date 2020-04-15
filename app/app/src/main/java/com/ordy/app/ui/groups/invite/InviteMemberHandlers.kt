package com.ordy.app.ui.groups.invite

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import okhttp3.ResponseBody

class InviteMemberHandlers(
    val activity: InviteMemberActivity,
    val viewModel: InviteMemberViewModel,
    val view: View,
    val groupId: Int
) {

    /**
     * Handle the invite button clicked
     */
    fun onInviteButtonClick(liveData: MutableLiveData<Query<ResponseBody>>, userId: Int) {
        if (liveData.value?.status != QueryStatus.LOADING) {
            viewModel.sendInviteToUserFromGroup(userId, groupId, liveData)
        }
    }
}