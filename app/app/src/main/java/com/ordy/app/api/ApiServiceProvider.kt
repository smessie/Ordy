package com.ordy.app.api

import android.content.Context
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceProvider {

    /**
     * Create the API Service
     */
    fun create(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create())
            .baseUrl("https://api.dev.geocode.ga/")
            .build()

        return retrofit.create(ApiService::class.java)
    }
}