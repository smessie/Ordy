package com.ordy.app.ui.orders.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Group
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import java.text.DateFormat
import java.util.*

class CreateOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

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
     * Get the MutableLiveData result of the Groups fetch.
     */
    fun getGroupsMLD(): MutableLiveData<Query<List<Group>>> {
        return repository.getGroups()
    }

    /**
     * Get the list of groups.
     */
    fun getGroups(): Query<List<Group>> {
        return getGroupsMLD().value!!
    }

    /**
     * Get the value of the location.
     */
    fun getLocationValue(): LocationInput {
        return locationValueData.value!!
    }

    /**
     * Refresh the list of groups the user is in.
     */
    fun refreshGroups() {
        repository.refreshGroups()
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

    /**
     * Get the MutableLiveData result of the Create order query.
     */
    fun getCreateOrderMLD(): MutableLiveData<Query<Order>> {
        return repository.getCreateOrderResult()
    }

    /**
     * Create a new order.
     * @param locationId: ID of the existing location for the order if applicable
     * @param customLocationName: Name of the custom location if applicable
     * @param deadline: Date of the deadline for new items to the order
     * @param groupId: ID of the group where the order belongs to
     */
    fun createOrder(locationId: Int?, customLocationName: String?, deadline: Date, groupId: Int?) {
        repository.createOrder(locationId, customLocationName, deadline, groupId)
    }
}