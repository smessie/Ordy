package com.ordy.app.api

import android.media.Image
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

interface ApiService {

    /**
     * Authentication
     */
    @POST("auth/login")
    fun login(@Body body: UserLogin): Observable<ResponseBody>

    @POST("auth/register")
    fun register(@Body body: UserRegister): Observable<ResponseBody>

    @POST("auth/logout")
    fun logout(): Observable<ResponseBody>

    /**
     * Locations
     */
    @GET("locations")
    fun locations(@Query("search") search: String): Observable<List<Location>>

    @GET("locations/{locationId}")
    fun location(@Path("locationId") locationId: Int): Observable<Location>

    @GET("locations/{locationId}/items")
    fun locationItems(@Path("locationId") locationId: Int): Observable<List<Item>>

    /**
     * Groups
     */
    @POST("groups")
    fun createGroup(@Body body: GroupCreate): Observable<Int>

    @PATCH("groups/{groupId}")
    fun updateGroup(@Path("groupId") groupId: Int, @Body body: GroupUpdate): Observable<Group>

    @POST("groups/{groupId}/invites/{userId}")
    fun createInviteGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @DELETE("groups/{groupId}/invites/{userId}")
    fun deleteInviteGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @DELETE("groups/{groupId}/members/{userId}")
    fun deleteMemberGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @GET("user/groups")
    fun userGroups(): Observable<List<Group>>

    @GET("user/invites")
    fun userInvites(): Observable<List<Group>>

    @POST("user/invites/{groupId}")
    fun userActionInvites(@Body inviteAction: InviteAction, @Path("groupId") groupId: Int): Observable<ResponseBody>

    @POST("user/invites/{groupId}/leave")
    fun userLeaveGroup(): Observable<ResponseBody>

    /**
     * Orders
     */
    @POST("/orders")
    fun createOrder(@Body body: OrderCreate): Observable<Order>

    @GET("/orders/{orderId}")
    fun order(@Path("orderId") orderId: Int): Observable<Order>

    @GET("/orders/{orderId}/bill")
    fun orderBill(@Path("orderId") orderId: Int): Observable<Image>

    @POST("/orders/{orderId}/bill")
    fun createOrderBill(@Path("orderId") orderId: Int, @Path("bill") bill: File): Observable<Order>

    @GET("/user/orders")
    fun userOrders(): Observable<List<Order>>

    @PATCH("/user/orders/{orderId}")
    fun userUpdateOrders(@Path("orderId") orderId: Int, @Body body: OrderUpdate): Observable<ResponseBody>

    @POST("/user/orders/{orderId}/items")
    fun userAddOrderItem(@Path("orderId") orderId: Int, @Body body: OrderAddItem): Observable<OrderItem>

    @PATCH("/user/orders/{orderId}/items/{orderItemId}")
    fun userUpdateOrderItem(@Path("orderId") orderId: Int, @Path("orderItemId") orderItemId: Int, @Body body: OrderUpdateItem): Observable<ResponseBody>

    @DELETE("/user/orders/{orderId}/items/{orderItemId}")
    fun userDeleteOrderItem(@Path("orderId") orderId: Int, @Path("orderItemId") orderItemId: Int): Observable<ResponseBody>

    /**
     * Payments
     */
    @GET("/user/payments/debtors")
    fun userDebtors(): Observable<List<Payment>>

    @GET("/user/payments/debts")
    fun userDepts(): Observable<List<Payment>>

    @PATCH("/user/payments/{orderId}/{userId}")
    fun userSetPaid(@Path("orderId") orderId: Int, @Path("userId") userId: Int, @Body body: PaymentUpdate): Observable<ResponseBody>

    @POST("/user/payments/{orderId}/{userId}/notification")
    fun userNotifyDeptor(@Path("orderId") orderId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

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
                .baseUrl("https://api.dev.ordy.ga/")
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