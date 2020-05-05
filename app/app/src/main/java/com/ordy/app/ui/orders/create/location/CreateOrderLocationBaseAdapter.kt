package com.ordy.app.ui.orders.create.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.orders.create.CreateOrderViewModel
import com.ordy.app.ui.orders.create.LocationInput
import kotlinx.android.synthetic.main.list_location_item.view.*
import kotlinx.android.synthetic.main.list_location_item_default.view.*

class CreateOrderLocationBaseAdapter(
    val context: Context,
    private val dialog: DialogFragment,
    val viewModel: CreateOrderLocationViewModel,
    private val activityViewModel: CreateOrderViewModel,
    private val listView: ListView
) : BaseAdapter() {

    private var defaultItemView = View.inflate(context, R.layout.list_location_item_default, null)

    init {
        // Set click handler for default view.
        defaultItemView.location_item_pick_custom.setOnClickListener {

            // Set the selected custom location
            activityViewModel.setLocationValue(
                LocationInput(
                    customLocationName = viewModel.getSearchValue()
                )
            )

            // Dismiss the dialog
            dialog.dismiss()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_location_item, parent, false)

        when (viewModel.getLocations().status) {

            QueryStatus.SUCCESS -> {
                val isFavorite = viewModel.getLocations().requireData()[position].favorite
                val location = viewModel.getLocations().requireData()[position].location

                // Assign the data.
                view.location_item_name.text = location.name
                view.location_item_address.text =
                    when (location.address) {
                        null -> "No address found"
                        else -> location.address
                    }

                view.location_favorite_mark.visibility =
                    when (isFavorite) {
                        false -> View.INVISIBLE
                        else -> View.VISIBLE
                    }

                // Set click handler.
                view.location_item_pick.setOnClickListener {

                    // Set the selected location
                    activityViewModel.setLocationValue(
                        LocationInput(
                            location
                        )
                    )

                    // Dismiss the dialog
                    dialog.dismiss()
                }
            }

            else -> {
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
            QueryStatus.SUCCESS -> viewModel.getLocations().requireData().size
            else -> 0
        }
    }

    override fun isEmpty(): Boolean {

        var hasFavorites = false
        if (viewModel.getLocations().status == QueryStatus.SUCCESS) {
            hasFavorites = viewModel.getLocations().requireData().isNotEmpty()
        }

        // Show the empty view when the search query is empty and the user has no matching favorite locations.
        // Done to show the footer for a custom location.
        return viewModel.getSearchValue().isEmpty() && !hasFavorites
    }

    fun update() {

        // Remove the footer and add it again to prevent errors
        listView.removeFooterView(defaultItemView)

        // Only add the footer if the search value is NOT empty
        if (viewModel.getSearchValue().isNotBlank()) {

            // Update the text of the default item.
            defaultItemView.location_item_default_text.text = String.format(
                dialog.resources.getString(
                    R.string.locations_default_text,
                    viewModel.getSearchValue()
                )
            )

            listView.addFooterView(defaultItemView)
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}