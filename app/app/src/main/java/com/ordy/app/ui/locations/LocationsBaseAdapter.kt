package com.ordy.app.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.api.wrappers.LocationWrapper
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.fragment_locations.view.*
import kotlinx.android.synthetic.main.list_location_card.view.*
import okhttp3.ResponseBody

class LocationsBaseAdapter(
    val context: Context,
    val viewModel: LocationsViewModel,
    private val lifecycleOwner: LifecycleOwner,
    val view: View
) : BaseAdapter() {

    private var locations: Query<List<LocationWrapper>> = Query()

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
            .inflate(R.layout.list_location_card, parent, false)

        when (locations.status) {

            QueryStatus.SUCCESS -> {
                val locationWrapper = viewModel.getLocations().requireData()[position]

                // this is needed to prevent strange behaviour of ListView that reuses ListCells
                view.favorite_mark.isSelected = false

                if (locationWrapper.favorite && !viewModel.isFavorite(locationWrapper.location.id)) {
                    viewModel.markAsFavorite(locationWrapper.location.id)
                }

                // if a location was already marked as favorite
                if (viewModel.isFavorite(locationWrapper.location.id)) {
                    view.favorite_mark.isSelected = true
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
                        viewModel.deleteFavoriteLocation(locationWrapper.location.id, favoriteResult)
                        viewModel.unMarkAsFavorite(locationWrapper.location.id)
                    } else {
                        viewModel.createFavoriteLocation(locationWrapper.location.id, favoriteResult)
                        viewModel.markAsFavorite(locationWrapper.location.id)
                    }

                    view.favorite_mark.isSelected = viewModel.isFavorite(locationWrapper.location.id)
                }

                // observe result of favoriteResult
                favoriteResult.observe(lifecycleOwner, Observer {
                    when (it.status) {

                        QueryStatus.LOADING -> {
                            // when loading, disable the favorite mark
                            view.favorite_mark.isClickable = false
                        }

                        QueryStatus.SUCCESS -> {
                            // when success, make the favorite mark selectable again
                            view.favorite_mark.isClickable = true
                        }

                        QueryStatus.ERROR -> {

                            // reset state as it was before the favorite mark was clicked
                            if (viewModel.isFavorite(locationWrapper.location.id)) {
                                viewModel.unMarkAsFavorite(locationWrapper.location.id)
                            } else {
                                viewModel.markAsFavorite(locationWrapper.location.id)
                            }

                            view.favorite_mark.isSelected = viewModel.isFavorite(locationWrapper.location.id)
                            ErrorHandler().handle(it.error, view)
                        }

                        else -> {}
                    }
                })
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
            QueryStatus.SUCCESS -> return when {
                // Do not show any results if the search query is blank and if the user has no favorite locations.
                viewModel.getLocations().requireData().isEmpty() -> 0
                else -> viewModel.getLocations().requireData().size
            }
            else -> 0
        }
    }
}