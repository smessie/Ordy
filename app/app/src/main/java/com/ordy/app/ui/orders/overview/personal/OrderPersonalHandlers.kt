package com.ordy.app.ui.orders.overview.personal

import android.content.Intent
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.ui.orders.overview.additem.AddItemOrderActivity

class OrderPersonalHandlers(val fragment: OrderPersonalFragment, val viewModel: OverviewOrderViewModel) {

    /**
     * Open the activity to add an item to the order.
     */
    fun onAddItemClick() {

        if(viewModel.order.value != null) {
            var query = viewModel.order.value!!

            if(query.status == QueryStatus.SUCCESS) {
                val intent = Intent(fragment.requireContext(), AddItemOrderActivity::class.java)

                // Pass the location id as extra information:
                intent.putExtra("location_id", query.requireData().location.id)

                fragment.requireContext().startActivity(intent)
            }
        }
    }
}