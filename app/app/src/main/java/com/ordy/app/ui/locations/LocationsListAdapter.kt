package com.ordy.app.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_location_card.view.*
import kotlinx.android.synthetic.main.list_location_item.view.*

class LocationsListAdapter(
    val context: Context,
    val viewModel: LocationsViewModel
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_card, parent, false)

        when (viewModel.getLocations().status) {

            QueryStatus.SUCCESS -> {
                val location = viewModel.getLocations().requireData()[position]

                view.favorite_mark.isSelected = false

                // if a location was already marked as favorite
                if (viewModel.isFavorite(location.id)) {
                    view.favorite_mark.isSelected = true
                }

                // Assign the data.
                view.location_name.text = location.name

                view.favorite_mark.setOnClickListener {
                    if (viewModel.isFavorite(location.id)) {
                        viewModel.unMarkAsFavorite(location.id)
                    } else {
                        viewModel.markAsFavorite(location.id)
                    }

                    view.favorite_mark.isSelected = viewModel.isFavorite(location.id)
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