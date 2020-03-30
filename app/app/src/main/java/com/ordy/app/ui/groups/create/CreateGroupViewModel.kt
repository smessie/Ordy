package com.ordy.app.ui.groups.create

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.util.Query

class CreateGroupViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {
    lateinit var rootView: View
    val createResult: MutableLiveData<Query<Int>> = MutableLiveData(Query())
    var handlingCreateRequest = false
}