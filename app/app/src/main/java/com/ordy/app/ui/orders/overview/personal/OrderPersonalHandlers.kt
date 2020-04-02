package com.ordy.app.ui.orders.overview.personal

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.actions.OrderUpdateItem
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.additem.AddItemOrderActivity
import okhttp3.ResponseBody

class OrderPersonalHandlers(val fragment: OrderPersonalFragment, val viewModel: OverviewOrderViewModel) {

    /**
     * Remove an item from a given order
     * @param livedata Object to bind result to
     * @param orderId Id of the order
     * @param orderItemId Id of the order item
     */
    fun removeItem(livedata: MutableLiveData<Query<ResponseBody>>, orderId: Int, orderItemId: Int) {
        FetchHandler.handle(
            livedata,
            viewModel.apiService.userDeleteOrderItem(
                orderId,
                orderItemId
            )
        )
    }

    /**
     * Update the comment of a given order.
     * @param livedata Object to bind result to
     * @param orderId Id of the order
     * @param orderItemId Id of the order item
     * @param comment Comment to set for the item.
     */
    fun updateItem(livedata: MutableLiveData<Query<ResponseBody>>, orderId: Int, orderItemId: Int, comment: String) {

        // Add the item to the order
        FetchHandler.handle(
            livedata,
            viewModel.apiService.userUpdateOrderItem(
                orderId,
                orderItemId,
                OrderUpdateItem(
                    comment
                )
            )
        )
    }

    /**
     * Open the activity to add an item to the order.
     */
    fun onAddItemClick() {

        if(viewModel.order.value != null) {
            var query = viewModel.order.value!!

            if(query.status == QueryStatus.SUCCESS) {
                val intent = Intent(fragment.requireContext(), AddItemOrderActivity::class.java)

                // Pass the location & order id as extra information:
                intent.putExtra("location_id", query.requireData().location.id)
                intent.putExtra("order_id", query.requireData().id)

                fragment.requireContext().startActivity(intent)
            }
        }
    }
}