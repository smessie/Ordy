package com.ordy.app.ui.groups.create

import android.view.View
import com.ordy.app.api.models.actions.GroupCreate
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroupHandlers(
    val activity: CreateGroupActivity,
    val viewModel: CreateGroupViewModel,
    val view: View
) {

    /**
     * Handle the leave button clicked
     */
    fun onCreateButtonClick() {
        if (viewModel.createResult.value?.status != QueryStatus.LOADING) {
            val groupName = InputUtil.extractText(activity.input_name)

            FetchHandler.handle(
                viewModel.createResult, viewModel.apiService.createGroup(
                    GroupCreate(groupName)
                )
            )
        } else {
            ErrorHandler.handleRawGeneral(
                "Calm down ;) another request is still processing...",
                view
            )
        }
    }
}