package com.ordy.app.ui.orders.overview

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.net.URI
import java.util.*

class OverviewOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

    val orderId = MutableLiveData(-1)

    var updateTimer: Timer? = null
  
    // Uri of the selected image when uploading from the camera.
    var billUploadUri: URI? = null

    val uploadBillMLD: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())
    val orderMLD: MutableLiveData<Query<Order>> = MutableLiveData(Query(QueryStatus.LOADING))

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