package com.ordy.app.ui.orders.overview

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.Repository
import com.ordy.app.api.RepositoryViewModel
import com.ordy.app.api.models.Order
import com.ordy.app.api.util.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.*
import java.lang.IllegalStateException
import java.net.URI

class OverviewOrderViewModel(repository: Repository) : RepositoryViewModel(repository) {

    val orderId = MutableLiveData(-1)

    var updateTimer: Timer? = null
  
    // Uri of the selected image when uploading from the camera.
    var billUploadUri: URI? = null

    /**
     * Get the MutableLiveData result of the Order fetch.
     */
    fun getOrderMLD(): MutableLiveData<Query<Order>> {
        return repository.getOrder()
    }

    /**
     * Refresh the order
     */
    fun refreshOrder() {
        repository.refreshOrder(orderId.value!!)
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
        repository.uploadBill(orderId, image)
    }

    /**
     * Get the MutableLiveData resultof the Upload bill query.
     */
    fun getUploadBillResult(): MutableLiveData<Query<ResponseBody>> {
        return repository.getUploadBillResult()
    }
}