package com.ordy.app.api

import android.content.Context


class RepositoryProvider {

    /**
     * Create the Repository
     */
    fun create(context: Context): Repository {
        return Repository(ApiServiceProvider().create(context))
    }
}
