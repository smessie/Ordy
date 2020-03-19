package com.ordy.app.api

import com.ordy.app.api.models.User
import com.ordy.app.api.models.UserLogin
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("users")
    fun users(): Observable<List<User>>

    @POST("auth/login")
    fun login(@Body body: UserLogin): Observable<Boolean>

    /**
     * Create function for creating the Api Service.
     */
    companion object {
        fun create(): ApiService {

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
}

/**
 * Global variable
 */
val apiService by lazy {
    ApiService.create()
}