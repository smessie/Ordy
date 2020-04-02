package com.ordy.app.ui.orders.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ordy.app.api.ApiService
import com.ordy.app.api.ApiServiceViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import java.text.DateFormat
import java.util.*

class CreateOrderViewModel(apiService: ApiService) : ApiServiceViewModel(apiService) {

    /**
     * List of groups for the user
     */
    val groups: MutableLiveData<Query<List<Group>>> =
        FetchHandler.handleLive(apiService.userGroups())

    /**
     * Value of the location input
     */
    val locationValueData: MutableLiveData<LocationInput> = MutableLiveData(LocationInput())

    /**
     * Value of the deadline input
     */
    val deadlineValueData: MutableLiveData<Date> = MutableLiveData(Date())

    /**
     * Value of the group input
     */
    val groupValueData: MutableLiveData<Group> = MutableLiveData()

    /**
     * Result of the "create order" query
     */
    val createOrderResult: MutableLiveData<Query<Order>> = MutableLiveData(Query())

    /**
     * Get the name of the location value.
     * When "location" is null it will return the "customLocationName"
     */
    fun locationValueName() = Transformations.map(locationValueData) {
        return@map when {
            it.location != null -> {
                it.location.name
            }
            it.customLocationName != null -> {
                it.customLocationName
            }
            else -> {
                ""
            }
        }
    }

    /**
     * Get the name of the deadline value.
     */
    fun deadlineValueName() = Transformations.map(deadlineValueData) {
        return@map DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(it)
    }

    /**
     * Get the list of groups.
     */
    fun getGroups(): Query<List<Group>> {
        return groups.value!!
    }

    /**
     * Get the value of the location.
     */
    fun getLocationValue(): LocationInput {
        return locationValueData.value!!
    }

    /**
     * Get the value of the deadline.
     */
    fun getDeadlineValue(): Date {
        return deadlineValueData.value!!
    }

    /**
     * Get the value of the group.
     */
    fun getGroupValue(): Group {
        return groupValueData.value!!
    }

    /**
     * Set the value of the location.
     */
    fun setLocationValue(locationInput: LocationInput) {
        locationValueData.postValue(locationInput)
    }

    /**
     * Set the value of the group.
     */
    fun setGroupValue(group: Group) {
        groupValueData.postValue(group)
    }
}