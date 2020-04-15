package com.ordy.app.ui.groups.create

import android.view.View
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
        if (viewModel.getCreateGroup().status != QueryStatus.LOADING) {
            val groupName = viewModel.getNameValue()

            viewModel.createGroup(groupName)
        }
    }
}