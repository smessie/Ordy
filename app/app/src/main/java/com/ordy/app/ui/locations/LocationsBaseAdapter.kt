package com.ordy.app.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.api.wrappers.LocationWrapper
import kotlinx.android.synthetic.main.fragment_locations.view.*
import kotlinx.android.synthetic.main.list_location_card.view.*
import okhttp3.ResponseBody

class LocationsBaseAdapter(
    val context: Context,
    val viewModel: LocationsViewModel,
    val fragment: LocationsFragment,
    val view: View
) : BaseAdapter() {

    private var locations: Query<List<LocationWrapper>> = Query()

    init {
        val searchLoading = view.locations_search_loading

        viewModel.getLocationsMLD().observe(fragment, Observer {

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

                    ErrorHandler().handle(it.error, fragment.activity)
                }

                else -> {
                }
            }

            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_card, parent, false)

        when (locations.status) {

            QueryStatus.SUCCESS -> {
                val locationWrapper = locations.requireData()[position]

                // Update the favorite star.
                updateFavoriteView(view, locationWrapper.favorite)

                val favoriteResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

                // Assign the data.
                view.location_name.text = locationWrapper.location.name

                view.location_address.text =
                    when (locationWrapper.location.address) {
                        null -> "No address found"
                        else -> locationWrapper.location.address
                    }

                // Local variable for storing if the click action
                // caused the location to become favorite or not
                var favorite = locationWrapper.favorite

                view.favorite_mark.setOnClickListener {

                    if (favorite) {
                        viewModel.deleteFavoriteLocation(
                            locationWrapper.location.id,
                            favoriteResult
                        )

                        favorite = false

                        // Update the favorite star.
                        updateFavoriteView(view, favorite)

                        view.favorite_mark.contentDescription =
                            context.getString(R.string.not_favorite_description)
                    } else {
                        viewModel.createFavoriteLocation(
                            locationWrapper.location.id,
                            favoriteResult
                        )

                        favorite = true

                        // Update the favorite star.
                        updateFavoriteView(view, favorite)

                        view.favorite_mark.contentDescription =
                            context.getString(R.string.is_favorite_description)
                    }
                }

                // Observe result of favoriteResult
                favoriteResult.observe(fragment, Observer {
                    when (it.status) {

                        QueryStatus.LOADING -> {
                            // Show a loading effect and hide the favorite mark
                            view.favorite_mark.visibility = View.GONE
                            view.favorite_loading.visibility = View.VISIBLE
                        }

                        QueryStatus.SUCCESS -> {

                            // Stop the loading effect and show the favorite mark again
                            view.favorite_mark.visibility = View.VISIBLE
                            view.favorite_loading.visibility = View.GONE
                        }

                        QueryStatus.ERROR -> {

                            // Stop the loading effect and show the favorite mark again
                            view.favorite_mark.visibility = View.VISIBLE
                            view.favorite_loading.visibility = View.GONE

                            // Reset the favorite star.
                            updateFavoriteView(view, !favorite)

                            ErrorHandler().handle(it.error, fragment.activity)
                        }

                        else -> {
                        }
                    }

                })
            }

            else -> {
            }
        }

        return view
    }

    fun update(locations: Query<List<LocationWrapper>>) {
        this.locations = locations
        notifyDataSetChanged()
    }

    /**
     * Update the favorite mark for a specific view
     * @param view View to update
     * @param favorite If the star should be filled or not
     */
    fun updateFavoriteView(view: View, favorite: Boolean) {
        if (favorite) {
            view.favorite_mark.isSelected = true
            view.favorite_mark.contentDescription =
                context.getString(R.string.is_favorite_description)
        } else {
            view.favorite_mark.isSelected = false
            view.favorite_mark.contentDescription =
                context.getString(R.string.not_favorite_description)
        }

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
            QueryStatus.SUCCESS -> viewModel.getLocations().requireData().size
            else -> 0
        }
    }
}