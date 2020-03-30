package com.ordy.app.ui.orders.overview.personal

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.util.FetchHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.additem.AddItemOrderActivity
import okhttp3.ResponseBody

class OrderPersonalHandlers(val fragment: OrderPersonalFragment, val viewModel: OverviewOrderViewModel) {

    /**
     * Remove an item from a given order
     * @param orderId Id of the order to add the item to
     * @param cuisineItemId Id of the cuisin item (or null when a custom item name is given)
     * @param name Custom item name (ignored when cuisineItemId is present)
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