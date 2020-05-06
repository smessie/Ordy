package com.ordy.app.ui.orders.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.lang.IllegalStateException
import java.net.URI
import java.util.*

class OverviewOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

    /**
     * Id of the current order.
     */
    val orderId = MutableLiveData(-1)

    /**
     * Timer for updating the closing time.
     */
    var updateTimer: Timer? = null

    /**
     * Uri of the selected image when uploading from the camera.
     */
    var billUploadUri: URI? = null

    private val uploadBillMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    private val orderMLD: MutableLiveData<Query<Order>> = MutableLiveData(Query(QueryStatus.LOADING))

    /**
     * Get livedata for uploading the bill.
     */
    fun getUploadBillMLD(): MutableLiveData<Query<ResponseBody>> {
        return this.uploadBillMLD
    }

    /**
     * Get livedata for the current order.
     */
    fun getOrderMLD(): MutableLiveData<Query<Order>> {
        return this.orderMLD
    }

    /**
     * Get query for the current order.
     * @throws IllegalStateException when MLD.value is null.
     */
    fun getOrder(): Query<Order> {
        return this.orderMLD.value ?: throw IllegalStateException("Order data is null")
    }

    /**
     * Refresh the order
     */
    fun refreshOrder() {
        repository.refreshOrder(orderMLD, orderId.value!!)
    }

    /**
     * Remove an item from a given order.
     * @param liveData: Object to bind result to
     * @param orderId: Id of the order
     * @param orderItemId: Id of the order item
     */
    fun removeItem(liveData: MutableLiveData<Query<ResponseBody>>, orderId: Int, orderItemId: Int) {
        repository.removeItem(liveData, orderId, orderItemId)
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
        repository.updateItem(liveData, orderId, orderItemId, comment)
    }

    /**
     * Upload a bill for a given order.
     * @param orderId: Id of the order
     * @param image: Body containing the image data
     */
    fun uploadBill(orderId: Int, image: MultipartBody.Part) {
        repository.uploadBill(uploadBillMLD, orderId, image)
    }
}