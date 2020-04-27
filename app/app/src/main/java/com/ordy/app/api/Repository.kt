package com.ordy.app.api

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.*
import com.ordy.app.api.models.actions.*
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
import okhttp3.ResponseBody
import java.util.*

class Repository(private val apiService: ApiService) {

    /******************************
     ***        GROUPS          ***
     ******************************/
    private val createGroupResult: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val inviteableUsers: MutableLiveData<Query<List<GroupInviteUserWrapper>>> = MutableLiveData(Query())
    private val group: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val renameGroupResult: MutableLiveData<Query<Group>> = MutableLiveData(Query())
    private val leaveGroupResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val removeMemberResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val groups: MutableLiveData<Query<List<Group>>> = MutableLiveData(Query())

    /**
     * Create a new group.
     * @param groupName: The name that the newly created group should have
     */
    fun createGroup(groupName: String) {
        FetchHandler.handle(createGroupResult, apiService.createGroup(GroupCreate(groupName)))
    }

    /**
     * Search for all matching users that are able to get an invite for the group.
     * @param groupId: ID of the group
     * @param username: The name we want to match on in our search query
     */
    fun searchMatchingInviteUsers(groupId: Int, username: String) {
        FetchHandler.handle(
            inviteableUsers,
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
    fun deleteInviteOfUserFromGroup(userInvitedId: Int,
                                    groupId: Int,
                                    liveData: MutableLiveData<Query<ResponseBody>>) {
        FetchHandler.handle(
            liveData, apiService.deleteInviteGroup(groupId, userInvitedId)
        )
    }

    /**
     * Refresh the group with given id.
     * @param groupId: ID of the group we want to fetch
     */
    fun refreshGroup(groupId: Int) {
        FetchHandler.handle(group, apiService.group(groupId))
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
    fun userLeaveGroup(groupId: Int) {
        FetchHandler.handle(
            leaveGroupResult,
            apiService.userLeaveGroup(groupId)
        )
    }

    /**
     * Change the name of a group.
     * @param groupId: ID of the group of which the name will be changed
     * @param newName: The new name that will be given to the group
     */
    fun renameGroup(groupId: Int, newName: String) {
        FetchHandler.handle(
            renameGroupResult,
            apiService.updateGroup(groupId, GroupUpdate(newName))
        )
    }

    /**
     * Remove a member from a group.
     * @param userId: ID of the user that should be kicked
     * @param groupId: ID of the group the user is removed from
     */
    fun removeMemberFromGroup(userId: Int, groupId: Int) {
        FetchHandler.handle(
            removeMemberResult, apiService.deleteMemberGroup(groupId, userId)
        )
    }

    /**
     * Get the MutableLiveData result of the Create group query.
     */
    fun getCreateGroupResult(): MutableLiveData<Query<Group>> {
        return createGroupResult
    }

    /**
     * Get the MutableLiveData result of all users matched that are able to invite.
     */
    fun getInviteableUsers(): MutableLiveData<Query<List<GroupInviteUserWrapper>>> {
        return inviteableUsers
    }

    /**
     * Get the MutableLiveData result of the Group fetch.
     */
    fun getGroup(): MutableLiveData<Query<Group>> {
        return group
    }

    /**
     * Get the MutableLiveData result of the Rename group query.
     */
    fun getRenameGroupResult(): MutableLiveData<Query<Group>> {
        return renameGroupResult
    }

    /**
     * Get the MutableLiveData result of the Leave group query.
     */
    fun getLeaveGroupResult(): MutableLiveData<Query<ResponseBody>> {
        return leaveGroupResult
    }

    /**
     * Get the MutableLiveData result of the Remove member from group query.
     */
    fun getRemoveMemberResult(): MutableLiveData<Query<ResponseBody>> {
        return removeMemberResult
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
    private val locations: MutableLiveData<Query<List<Location>>> = MutableLiveData(Query())

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
    fun getLocationsResult(): MutableLiveData<Query<List<Location>>> {
        return locations
    }

    /******************************
     ***         LOGIN          ***
     ******************************/
    private val loginResult: MutableLiveData<Query<LoginResponse>> = MutableLiveData(Query())
    private val registerResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

    /**
     * Attempt to login a user.
     * @param email: Email entered by the user
     * @param password: Password entered by the user
     */
    fun login(email: String, password: String) {
        FetchHandler.handle(
            loginResult, apiService.login(
                UserLogin(
                    email,
                    password
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
    fun register(username: String, email: String, password: String) {
        FetchHandler.handle(
            registerResult, apiService.register(
                UserRegister(
                    username,
                    email,
                    password
                )
            )
        )
    }

    /**
     * Get the MutableLiveData result of the Login query.
     */
    fun getLoginResult(): MutableLiveData<Query<LoginResponse>> {
        return loginResult
    }

    /**
     * Get the MutableLiveData result of the Register query.
     */
    fun getRegisterResult(): MutableLiveData<Query<ResponseBody>> {
        return registerResult
    }

    /******************************
     ***        ORDERS          ***
     ******************************/
    private val orders: MutableLiveData<Query<List<Order>>> = MutableLiveData(Query())
    private val createOrderResult: MutableLiveData<Query<Order>> = MutableLiveData(Query())
    private val order: MutableLiveData<Query<Order>> = MutableLiveData(Query(QueryStatus.LOADING))
    private val cuisineItems: MutableLiveData<Query<List<Item>>> = MutableLiveData(Query())
    private val addItemResult: MutableLiveData<Query<OrderItem>> = MutableLiveData(Query())

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
    fun createOrder(locationId: Int?, customLocationName: String?, deadline: Date, groupId: Int?) {
        FetchHandler.handle(
            createOrderResult,
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
    fun refreshOrder(orderId: Int) {
        FetchHandler.handle(order, apiService.order(orderId))
    }

    /**
     * Refresh the cuisine items.
     */
    fun refreshCuisineItems(locationId: Int) {
        FetchHandler.handle(cuisineItems, apiService.locationItems(locationId))
    }

    /**
     * Add a new item to a given order.
     * @param orderId: Id of the order to add the item to
     * @param cuisineItemId: Id of the cuisine item (or null when a custom item name is given)
     * @param name: Custom item name (ignored when cuisineItemId is present)
     */
    fun addItem(orderId: Int, cuisineItemId: Int?, name: String?) {
        FetchHandler.handle(
            addItemResult,
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
     * Get the MutableLiveData result of the Orders query.
     */
    fun getOrdersResult(): MutableLiveData<Query<List<Order>>> {
        return orders
    }

    /**
     * Get the MutableLiveData result of the Create order query.
     */
    fun getCreateOrderResult(): MutableLiveData<Query<Order>> {
        return createOrderResult
    }

    /**
     * Get the MutableLiveData result of the Order fetch.
     */
    fun getOrder(): MutableLiveData<Query<Order>> {
        return order
    }

    /**
     * Get the MutableLiveData result of the Cuisine items fetch.
     */
    fun getCuisineItems(): MutableLiveData<Query<List<Item>>> {
        return cuisineItems
    }

    /**
     * Get the MutableLiveData result of the Add item query.
     */
    fun getAddItemResult(): MutableLiveData<Query<OrderItem>> {
        return addItemResult
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

    /******************************
     ***        PROFILE         ***
     ******************************/
    private val invites: MutableLiveData<Query<List<GroupInvite>>> = MutableLiveData(Query())

    /**
     * Refresh the invites.
     */
    fun refreshInvites() {
        FetchHandler.handle(invites, apiService.userInvites())
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

    /**
     * Get the MutableLiveData result of the Invites fetch.
     */
    fun getInvites(): MutableLiveData<Query<List<GroupInvite>>> {
        return invites
    }

    /******************************
     ***        SETTINGS        ***
     ******************************/
}