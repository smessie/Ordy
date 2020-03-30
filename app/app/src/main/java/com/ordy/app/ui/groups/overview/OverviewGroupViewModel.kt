package com.ordy.app.ui.groups.overview

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import okhttp3.ResponseBody

class OverviewGroupViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    val group: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    val removeResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    var handlingRemoveRequest = false
    var rootView: View? = null

    fun removeMember(groupId: Int, userId: Int) {
        if (!handlingRemoveRequest) {
            handlingRemoveRequest = true
            FetchHandler.handle(
                removeResult, apiService.deleteMemberGroup(groupId, userId)
            )
        } else {
            if (rootView != null) {
                ErrorHandler.handleRawGeneral(
                    "Calm down ;) another request is still processing...",
                    rootView!!
                )
            }
        }
    }
}