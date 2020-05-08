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

                // This is needed to prevent strange behaviour of ListView that reuses ListCells
                view.favorite_mark.isSelected = false
                view.favorite_mark.contentDescription =
                    context.getString(R.string.not_favorite_description)

                // Add initial favorite locations to viewModel local favorite locations list
                if (locationWrapper.favorite && !viewModel.isFavorite(locationWrapper.location.id)) {
                    viewModel.markAsFavorite(locationWrapper.location.id)
                }

                // If a location is marked as favorite
                if (viewModel.isFavorite(locationWrapper.location.id)) {
                    view.favorite_mark.isSelected = true
                    view.favorite_mark.contentDescription =
                        context.getString(R.string.is_favorite_description)
                }

                val favoriteResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

                // Assign the data.
                view.location_name.text = locationWrapper.location.name

                view.location_address.text =
                    when (locationWrapper.location.address) {
                        null -> "No address found"
                        else -> locationWrapper.location.address
                    }

                view.favorite_mark.setOnClickListener {

                    if (viewModel.isFavorite(locationWrapper.location.id)) {
                        viewModel.deleteFavoriteLocation(
                            locationWrapper.location.id,
                            favoriteResult
                        )
                        viewModel.unMarkAsFavorite(locationWrapper.location.id)
                        view.favorite_mark.contentDescription =
                            context.getString(R.string.not_favorite_description)
                    } else {
                        viewModel.createFavoriteLocation(
                            locationWrapper.location.id,
                            favoriteResult
                        )
                        viewModel.markAsFavorite(locationWrapper.location.id)
                        view.favorite_mark.contentDescription =
                            context.getString(R.string.is_favorite_description)
                    }

                    view.favorite_mark.isSelected =
                        viewModel.isFavorite(locationWrapper.location.id)
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

                            // Reset state as it was before the favorite mark was clicked
                            if (viewModel.isFavorite(locationWrapper.location.id)) {
                                viewModel.unMarkAsFavorite(locationWrapper.location.id)
                                view.favorite_mark.contentDescription =
                                    context.getString(R.string.not_favorite_description)
                            } else {
                                viewModel.markAsFavorite(locationWrapper.location.id)
                                view.favorite_mark.contentDescription =
                                    context.getString(R.string.is_favorite_description)
                            }

                            view.favorite_mark.isSelected =
                                viewModel.isFavorite(locationWrapper.location.id)

                            // Stop the loading effect and show the favorite mark again
                            view.favorite_mark.visibility = View.VISIBLE
                            view.favorite_loading.visibility = View.GONE

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