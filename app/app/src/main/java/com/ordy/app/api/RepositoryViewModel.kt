package com.ordy.app.api

import android.content.Context
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


open class RepositoryViewModel(
    val repository: Repository
) : ViewModel()

class RepositoryViewModelFactory(
    private val context: Context
) :
    ViewModelProvider.Factory {

    @NonNull
    override fun <T : ViewModel?> create(@NonNull modelClass: Class<T>): T {
        return modelClass.getConstructor(Repository::class.java)
            .newInstance(RepositoryProvider().create(context))
    }
}