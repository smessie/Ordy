package com.ordy.app.api

import android.content.Context
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Deprecated("Should be refactored now to use RepositoryViewModel.")
open class ApiServiceViewModel(
    val apiService: ApiService
) : ViewModel()

@Deprecated("Should be refactored now to use RepositoryViewModelFactory.")
class ApiServiceViewModelFactory(
    private val context: Context
) :
    ViewModelProvider.Factory {

    @NonNull
    override fun <T : ViewModel?> create(@NonNull modelClass: Class<T>): T {
        return modelClass.getConstructor(ApiService::class.java).newInstance(ApiServiceProvider().create(context))
    }
}