package com.ordy.app.api

import android.content.Context
import android.media.Image
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
import com.ordy.app.api.wrappers.LocationWrapper
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File

interface ApiService {

    /**
     * Authentication
     */
    @POST("auth/login")
    fun login(@Body body: UserLogin): Observable<LoginResponse>

    @POST("auth/register")
    fun register(@Body body: UserRegister): Observable<ResponseBody>

    @POST("auth/logout")
    fun logout(): Observable<ResponseBody>

    /**
     * Locations
     */
    @GET("locations")
    fun locations(@Query("q") search: String): Observable<List<LocationWrapper>>

    @GET("locations/{locationId}")
    fun location(@Path("locationId") locationId: Int): Observable<Location>

    @GET("locations/{locationId}/items")
    fun locationItems(@Path("locationId") locationId: Int): Observable<List<Item>>

    /**
     * Groups
     */
    @POST("groups")
    fun createGroup(@Body body: GroupCreate): Observable<Group>

    @GET("groups/{groupId}")
    fun group(@Path("groupId") groupId: Int): Observable<Group>

    @PATCH("groups/{groupId}")
    fun updateGroup(@Path("groupId") groupId: Int, @Body body: GroupUpdate): Observable<Group>

    @GET("groups/{groupId}/invites/search/{username}")
    fun searchMatchingInviteUsers(@Path("groupId") groupId: Int, @Path("username") username: String): Observable<List<GroupInviteUserWrapper>>

    @POST("groups/{groupId}/invites/{userId}")
    fun createInviteGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @DELETE("groups/{groupId}/invites/{userId}")
    fun deleteInviteGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @DELETE("groups/{groupId}/members/{userId}")
    fun deleteMemberGroup(@Path("groupId") groupId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    @GET("user/groups")
    fun userGroups(): Observable<List<Group>>

    @GET("user/invites")
    fun userInvites(): Observable<List<GroupInvite>>

    @POST("user/invites/{groupId}")
    fun userActionInvites(@Body inviteAction: InviteAction, @Path("groupId") groupId: Int): Observable<ResponseBody>

    @POST("user/groups/{groupId}/leave")
    fun userLeaveGroup(@Path("groupId") groupId: Int): Observable<ResponseBody>

    /**
     * Orders
     */
    @POST("orders")
    fun createOrder(@Body body: OrderCreate): Observable<Order>

    @GET("orders/{orderId}")
    fun order(@Path("orderId") orderId: Int): Observable<Order>

    @GET("orders/{orderId}/bill")
    fun orderBill(@Path("orderId") orderId: Int): Observable<Image>

    @POST("orders/{orderId}/bill")
    fun createOrderBill(@Path("orderId") orderId: Int, @Path("bill") bill: File): Observable<Order>

    @GET("user/orders")
    fun userOrders(): Observable<List<Order>>

    @PATCH("user/orders/{orderId}")
    fun userUpdateOrders(@Path("orderId") orderId: Int, @Body body: OrderUpdate): Observable<ResponseBody>

    @POST("user/orders/{orderId}/items")
    fun userAddOrderItem(@Path("orderId") orderId: Int, @Body body: OrderAddItem): Observable<OrderItem>

    @PATCH("user/orders/{orderId}/items/{orderItemId}")
    fun userUpdateOrderItem(@Path("orderId") orderId: Int, @Path("orderItemId") orderItemId: Int, @Body body: OrderUpdateItem): Observable<ResponseBody>

    @DELETE("user/orders/{orderId}/items/{orderItemId}")
    fun userDeleteOrderItem(@Path("orderId") orderId: Int, @Path("orderItemId") orderItemId: Int): Observable<ResponseBody>

    /**
     * Payments
     */
    @GET("user/payments/debtors")
    fun userDebtors(): Observable<List<Payment>>

    @GET("user/payments/debts")
    fun userDepts(): Observable<List<Payment>>

    @PATCH("user/payments/{orderId}/{userId}")
    fun userSetPaid(@Path("orderId") orderId: Int, @Path("userId") userId: Int, @Body body: PaymentUpdate): Observable<ResponseBody>

    @POST("user/payments/{orderId}/{userId}/notification")
    fun userNotifyDeptor(@Path("orderId") orderId: Int, @Path("userId") userId: Int): Observable<ResponseBody>

    fun create(context: Context)
}