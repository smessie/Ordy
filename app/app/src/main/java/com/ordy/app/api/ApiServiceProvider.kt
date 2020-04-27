package com.ordy.app.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.ordy.app.AppPreferences
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ApiServiceProvider {

    /**
     * Get the client for sending requests.
     */
    fun client(context: Context): OkHttpClient {
        // Add the "Authorization"-header to every request send to the backend
        return OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", AppPreferences(context).accessToken)
                .build()
            chain.proceed(newRequest)
        }.build()
    }

    /**
     * Create the API Service
     */
    fun create(context: Context): ApiService {

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")
            .create()

        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client(context))
            .baseUrl("https://api.ordy.ga")
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
