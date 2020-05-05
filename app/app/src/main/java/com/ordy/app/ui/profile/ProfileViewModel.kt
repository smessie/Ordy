package com.ordy.app.ui.profile

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.GroupInvite
import com.ordy.app.api.models.actions.InviteAction
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class ProfileViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Get the MutableLiveData result of the Invites fetch.
     */
    fun getInvitesMLD(): MutableLiveData<Query<List<GroupInvite>>> {
        return repository.getInvites()
    }

    /**
     * Refresh the invites.
     */
    fun refreshInvites() {
        repository.refreshInvites()
    }

    /**
     * Accept or decline an invite.
     * @param inviteAction: The action that should be executed (accept/decline)
     * @param groupId: ID of the group of the invite
     * @param actionInviteResult: Object where we want to store the result of our query in
     */
    fun userActionInvites(
        inviteAction: InviteAction,
        groupId: Int,
        actionInviteResult: MutableLiveData<Query<ResponseBody>>
    ) {
        repository.userActionInvites(inviteAction, groupId, actionInviteResult)
        // refresh group in order to add new group to cached results
        repository.refreshGroups()
    }
}