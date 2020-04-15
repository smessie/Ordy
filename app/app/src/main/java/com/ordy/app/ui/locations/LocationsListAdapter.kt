package com.ordy.app.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_location_item.view.*

class LocationsListAdapter(
    val context: Context,
    val viewModel: LocationsViewModel
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_item, parent, false)

        when (viewModel.getLocations().status) {

            QueryStatus.SUCCESS -> {
                val location = viewModel.getLocations().requireData()[position]

                // Assign the data.
                view.location_item_name.text = location.name
                view.location_item_address.text =
                    when (location.address) {
                        null -> "No address found"
                        else -> location.address
                    }

                // Hide the pick button
                view.location_item_pick.visibility = View.GONE
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

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    override fun getCount(): Int {
        return when (viewModel.getLocations().status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> return when {
                // Do not show any results for a blank search query.
                viewModel.getSearchValue().isEmpty() -> 0
                else -> viewModel.getLocations().requireData().size
            }
            else -> 0
        }
    }
}