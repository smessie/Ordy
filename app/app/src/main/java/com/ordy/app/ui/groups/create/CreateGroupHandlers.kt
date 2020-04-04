package com.ordy.app.ui.groups.create

import android.view.View
import com.ordy.app.api.models.actions.GroupCreate
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus

class CreateGroupHandlers(
    val activity: CreateGroupActivity,
    val viewModel: CreateGroupViewModel,
    val view: View
) {

    /**
     * Handle the leave button clicked
     */
    fun onCreateButtonClick() {
        if (viewModel.repository.getCreateGroupResult().value?.status != QueryStatus.LOADING) {
            val groupName = viewModel.getNameValue()

            viewModel.repository.createGroup(groupName)
        }
    }
}