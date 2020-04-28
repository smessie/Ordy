package com.ordy.app.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Location
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.fragment_locations.view.*
import kotlinx.android.synthetic.main.list_location_item.view.*

class LocationsBaseAdapter(
    val context: Context,
    val viewModel: LocationsViewModel,
    lifecycleOwner: LifecycleOwner,
    val view: View
) : BaseAdapter() {

    private var locations: Query<List<Location>> = Query()

    init {
        val searchLoading = view.locations_search_loading

        viewModel.getLocationsMLD().observe(lifecycleOwner, Observer {

            // Show a loading indicator in the searchbox.
            // Hide the list view while loading.
            when (it.status) {
                QueryStatus.LOADING -> {
                    searchLoading.visibility = View.VISIBLE
                    view.locations.emptyView = null
                }

                QueryStatus.SUCCESS -> {
                    searchLoading.visibility = View.INVISIBLE
                    view.locations.emptyView = view.locations_empty
                }

                QueryStatus.ERROR -> {
                    searchLoading.visibility = View.INVISIBLE

                    ErrorHandler().handle(it.error, view)
                }

                else -> {
                }
            }

            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_item, parent, false)

        when (locations.status) {

            QueryStatus.SUCCESS -> {
                val location = locations.requireData()[position]

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
            else -> {
            }
        }

        return view
    }

    fun update(locations: Query<List<Location>>) {
        this.locations = locations
        notifyDataSetChanged()
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
        return when (locations.status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> return when {
                // Do not show any results for a blank search query.
                viewModel.getSearchValue().isEmpty() -> 0
                else -> locations.requireData().size
            }
            else -> 0
        }
    }
}