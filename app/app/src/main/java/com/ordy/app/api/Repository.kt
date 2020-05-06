package com.ordy.app.api

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
import com.ordy.app.api.wrappers.LocationWrapper
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.util.*

open class Repository(private val apiService: ApiService) {

    /******************************
     ***        GROUPS          ***
     ******************************/
    private val groups: MutableLiveData<Query<List<Group>>> = MutableLiveData(Query())

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(liveData: MutableLiveData<Query<Group>>, groupName: String) {
        FetchHandler.handle(liveData, apiService.createGroup(GroupCreate(groupName)))
    }

    /**
     * Search for all matching users that are able to get an invite for the group.
     * @param groupId: ID of the group
     * @param username: The name we want to match on in our search query
     */
    fun searchMatchingInviteUsers(
        liveData: MutableLiveData<Query<List<GroupInviteUserWrapper>>>,
        groupId: Int,
        username: String
    ) {
        FetchHandler.handle(
            liveData,
            apiService.searchMatchingInviteUsers(groupId, username)
        )
    }

    /**
     * Send an invite for a group to an user.
     * @param userId: ID of the user we want to invite
     * @param groupId: ID of the group we want to send an invite for
     * @param liveData: Object where we want to store the result of our query in
     */
    fun sendInviteToUserFromGroup(
        userId: Int,
        groupId: Int,
        liveData: MutableLiveData<Query<ResponseBody>>
    ) {
        FetchHandler.handle(
            liveData, apiService.createInviteGroup(groupId, userId)
        )
    }

    /**
     * Delete an invite send to an user.
     * @param userInvitedId: ID of the user we want to delete his invite
     * @param groupId: ID of the group we want to delete the invite for
     * @param liveData: Object where we want to store the result of our query in
     */
    fun deleteInviteOfUserFromGroup(
        userInvitedId: Int,
        groupId: Int,
        liveData: MutableLiveData<Query<ResponseBody>>
    ) {
        FetchHandler.handle(
            liveData, apiService.deleteInviteGroup(groupId, userInvitedId)
        )
    }

    /**
     * Refresh the group with given id.
     * @param groupId: ID of the group we want to fetch
     */
    fun refreshGroup(liveData: MutableLiveData<Query<Group>>, groupId: Int) {
        FetchHandler.handle(liveData, apiService.group(groupId))
    }

    /**
     * Refresh the list of groups the user is in.
     */
    fun refreshGroups() {
        FetchHandler.handle(groups, apiService.userGroups())
    }

    /**
     * Let the user leave the given group.
     * @param groupId: ID of the group the user is about to leave
     */
    fun userLeaveGroup(liveData: MutableLiveData<Query<ResponseBody>>, groupId: Int) {
        FetchHandler.handle(
            liveData,
            apiService.userLeaveGroup(groupId)
        )
    }

    /**
     * Change the name of a group.
     * @param groupId: ID of the group of which the name will be changed
     * @param newName: The new name that will be given to the group
     */
    fun renameGroup(liveData: MutableLiveData<Query<Group>>, groupId: Int, newName: String) {
        FetchHandler.handle(
            liveData,
            apiService.updateGroup(groupId, GroupUpdate(newName))
        )
    }

    /**
     * Remove a member from a group.
     * @param userId: ID of the user that should be kicked
     * @param groupId: ID of the group the user is removed from
     */
    fun removeMemberFromGroup(
        liveData: MutableLiveData<Query<ResponseBody>>,
        userId: Int,
        groupId: Int
    ) {
        FetchHandler.handle(
            liveData, apiService.deleteMemberGroup(groupId, userId)
        )
    }

    /**
     * Get the MutableLiveData result of the Groups fetch.
     */
    fun getGroups(): MutableLiveData<Query<List<Group>>> {
        return groups
    }

    /******************************
     ***       LOCATIONS        ***
     ******************************/
    private val locations: MutableLiveData<Query<List<LocationWrapper>>> = MutableLiveData(Query())

    /**
     * Update the locations by the given search query.
     * @param searchValue: The name we want to match on in our search query
     */
    fun updateLocations(searchValue: String) {
        FetchHandler.handle(locations, apiService.locations(searchValue))
    }

    /**
     * Get the MutableLiveData result of the Locations fetch.
     */
    fun getLocationsResult(): MutableLiveData<Query<List<LocationWrapper>>> {
        return locations
    }

    /**
     * Add a location to the favorite location list of the user.
     * @param locationId: ID of location the user want to mark as favorite
     * @param liveData: Object where we want to store the result of our query in
     */
    fun markLocationAsFavorite(locationId: Int, liveData: MutableLiveData<Query<ResponseBody>>) {
        FetchHandler.handle(liveData, apiService.markLocationAsFavorite(locationId))
    }

    /**
     * Remove a location from a user favorite locations list
     * @param locationId: ID of location the user want to remove from his favorite location list
     * @param liveData: Object where we want to store the result of our query in
     */
    fun unMarkLocationAsFavorite(locationId: Int, liveData: MutableLiveData<Query<ResponseBody>>) {
        FetchHandler.handle(liveData, apiService.unMarkLocationAsFavorite(locationId))
    }

    /******************************
     ***         LOGIN          ***
     ******************************/

    /**
     * Attempt to login a user.
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     * @param deviceToken: Devicetoken of the user
     */
    fun login(
        liveData: MutableLiveData<Query<LoginResponse>>,
        email: String,
        password: String,
        deviceToken: String
    ) {
        FetchHandler.handle(
            liveData, apiService.login(
                UserLogin(
                    email,
                    password,
                    deviceToken
                )
            )
        )
    }

    /**
     * Attempt to register a user.
     * @param username: Username entered by the user
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun register(
        liveData: MutableLiveData<Query<ResponseBody>>,
        username: String,
        email: String,
        password: String
    ) {
        FetchHandler.handle(
            liveData, apiService.register(
                UserRegister(
                    username,
                    email,
                    password
                )
            )
        )
    }

    /******************************
     ***        ORDERS          ***
     ******************************/
    private val orders: MutableLiveData<Query<List<Order>>> = MutableLiveData(Query())

    /**
     * Refresh the list of orders.
     */
    fun refreshOrders() {
        FetchHandler.handle(orders, apiService.userOrders())
    }

    /**
     * Create a new order.
     * @param locationId: ID of the existing location for the order if applicable
     * @param customLocationName: Name of the custom location if applicable
     * @param deadline: Date of the deadline for new items to the order
     * @param groupId: ID of the group where the order belongs to
     */
    fun createOrder(
        createOrderMLD: MutableLiveData<Query<Order>>,
        locationId: Int?,
        customLocationName: String?,
        deadline: Date,
        groupId: Int?
    ) {
        FetchHandler.handle(
            createOrderMLD,
            apiService.createOrder(
                OrderCreate(
                    locationId = locationId,
                    customLocationName = customLocationName,
                    deadline = deadline,
                    groupId = groupId
                )
            )
        )
    }

    /**
     * Refresh the order.
     */
    fun refreshOrder(liveData: MutableLiveData<Query<Order>>, orderId: Int) {
        FetchHandler.handle(liveData, apiService.order(orderId))
    }

    /**
     * Refresh the cuisine items.
     */
    fun refreshCuisineItems(liveData: MutableLiveData<Query<List<Item>>>, locationId: Int) {
        FetchHandler.handle(liveData, apiService.locationItems(locationId))
    }

    /**
     * Add a new item to a given order.
     * @param orderId: Id of the order to add the item to
     * @param cuisineItemId: Id of the cuisine item (or null when a custom item name is given)
     * @param name: Custom item name (ignored when cuisineItemId is present)
     */
    fun addItem(
        liveData: MutableLiveData<Query<OrderItem>>,
        orderId: Int,
        cuisineItemId: Int?,
        name: String?
    ) {
        FetchHandler.handle(
            liveData,
            apiService.userAddOrderItem(
                orderId,
                OrderAddItem(
                    cuisineItemId,
                    name
                )
            )
        )
    }

    /**
     * Remove an item from a given order.
     * @param liveData: Object to bind result to
     * @param orderId: Id of the order
     * @param orderItemId: Id of the order item
     */
    fun removeItem(liveData: MutableLiveData<Query<ResponseBody>>, orderId: Int, orderItemId: Int) {
        FetchHandler.handle(
            liveData,
            apiService.userDeleteOrderItem(
                orderId,
                orderItemId
            )
        )
    }

    /**
     * Update the comment of a given order.
     * @param liveData: Object to bind result to
     * @param orderId: Id of the order
     * @param orderItemId: Id of the order item
     * @param comment: Comment to set for the item
     */
    fun updateItem(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        orderItemId: Int,
        comment: String
    ) {
        FetchHandler.handle(
            liveData,
            apiService.userUpdateOrderItem(
                orderId,
                orderItemId,
                OrderUpdateItem(
                    comment
                )
            )
        )
    }

    /**
     * Upload a bill for a given order.
     * @param orderId: Id of the order
     * @param image: Body containing the image data
     */
    fun uploadBill(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        image: MultipartBody.Part
    ) {
        FetchHandler.handle(
            liveData,
            apiService.createOrderBill(orderId, image)
        )
    }

    /**
     * Get the MutableLiveData result of the Orders query.
     */
    fun getOrdersResult(): MutableLiveData<Query<List<Order>>> {
        return orders
    }

    /******************************
     ***       PAYMENTS         ***
     ******************************/
    val userDebtorsResult: MutableLiveData<Query<List<Payment>>> = MutableLiveData(Query())
    val userDebtsResult: MutableLiveData<Query<List<Payment>>> = MutableLiveData(Query())

    /**
     * Refresh the Debtors.
     * */
    fun refreshDebtors() {
        FetchHandler.handle(
            userDebtorsResult, apiService.userDebtors()
        )
    }

    /**
     * Refresh the User his debts.
     * */
    fun refreshDebts() {
        FetchHandler.handle(
            userDebtsResult, apiService.userDepts()
        )
    }

    /**
     * Update The payment status of an order.
     * */
    fun updatePaid(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        userId: Int,
        paymentUpdate: PaymentUpdate
    ) {
        FetchHandler.handle(
            liveData, apiService.userSetPaid(
                orderId = orderId,
                userId = userId,
                body = paymentUpdate
            )
        )
    }

    /**
     * Send a notification to the selected debtor
     */
    fun notifyDebtor(
        liveData: MutableLiveData<Query<ResponseBody>>,
        orderId: Int,
        userId: Int
    ) {
        FetchHandler.handle(
            liveData, apiService.userNotifyDeptor(
                orderId, userId
            )
        )
    }

    /******************************
     ***        PROFILE         ***
     ******************************/

    /**
     * Refresh the invites.
     */
    fun refreshInvites(liveData: MutableLiveData<Query<List<GroupInvite>>>) {
        FetchHandler.handle(liveData, apiService.userInvites())
    }

    /**
     * Accept or decline an invite.
     * @param inviteAction: The action that should be executed (accept/decline)
     * @param groupId: ID of the group of the invite
     * @param actionInviteResult: Object where we want to store the result of our query in
     */
    fun userActionInvites(
        inviteAction: InviteAction,
        groupId: Int,
        actionInviteResult: MutableLiveData<Query<ResponseBody>>
    ) {
        FetchHandler.handle(
            actionInviteResult, apiService.userActionInvites(
                inviteAction, groupId
            )
        )
    }
}