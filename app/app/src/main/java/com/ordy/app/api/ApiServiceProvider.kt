package com.ordy.app.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.ordy.app.AppPreferences
import com.ordy.app.api.util.FetchHandler
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ApiServiceProvider {

    /**
     * Get the standard builder for the API Service.
     */
    fun builder(): Retrofit.Builder {

        // Set the date format for the GSON converter.
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ")
            .create()

        return Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl("https://api.ordy.ga")
    }

    /**
     * Create the API Service
     */
    fun client(context: Context): OkHttpClient {
        // Defining the cache.
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)

        // Add the "Authorization"-header to every request send to the backend.
        return OkHttpClient.Builder()
            // Specify the cache we created earlier.
            .cache(myCache)
            // Add an Interceptor to the OkHttpClient.
            .addInterceptor { chain ->
                // Get the request from the chain.
                val request = chain.request()

                val newBuilder: Request.Builder = request.newBuilder()

                if (FetchHandler.hasNetwork(context)!!) {
                    /*
                    *  If there is Internet, get the cache that was stored 5 seconds ago.
                    *  If the cache is older than 5 seconds, then discard it,
                    *  and indicate an error in fetching the response.
                    *  The 'max-age' attribute is responsible for this behavior.
                    */
                    newBuilder.addHeader("Cache-Control", "public, max-age=" + 5)
                } else {
                    /*
                        *  If there is no Internet, get the cache that was stored 7 days ago.
                        *  If the cache is older than 7 days, then discard it,
                        *  and indicate an error in fetching the response.
                        *  The 'max-stale' attribute is responsible for this behavior.
                        *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                        */
                    newBuilder.addHeader(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    )
                }

                val newRequest: Request = newBuilder
                    .addHeader("Authorization", AppPreferences(context).accessToken)
                    .build()
                chain.proceed(newRequest)
            }.build()
    }

    /**
     * Create the API Service
     */
    fun create(context: Context): ApiService {
        return this.builder()
            .client(client(context))
            .build()
            .create(ApiService::class.java)
    }
}
