package com.ordy.app.ui.orders.create.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.DialogFragment
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.create.CreateOrderViewModel
import com.ordy.app.ui.orders.create.LocationInput
import kotlinx.android.synthetic.main.list_location_item.view.*

class CreateOrderLocationListAdapter(
    val context: Context,
    val dialog: DialogFragment,
    val viewModel: CreateOrderLocationViewModel,
    val activityViewModel: CreateOrderViewModel
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_item, parent, false)

        when (viewModel.getLocations().status) {

            QueryStatus.SUCCESS -> {
                val location = viewModel.getLocations().requireData()[position]

                // Assign the data.
                view.location_item_name.text = location.name

                // Set click handler.
                view.location_item_pick.setOnClickListener {

                    // Set the selected location
                    activityViewModel.setLocationValue(LocationInput(
                        location
                    ))

                    // Dismiss the dialog
                    dialog.dismiss()
                }
            }
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return when (viewModel.getLocations().status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> return when {

                // Do not show any results for a blank search query.
                viewModel.getSearchValue().isEmpty() -> {
                    0
                }
                else -> {
                    viewModel.getLocations().requireData().size
                }
            }
            else -> 0
        }
    }

}