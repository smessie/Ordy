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

class CreateOrderLocationListAdapter(
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
                        else  -> View.VISIBLE
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

    override fun isEmpty(): Boolean {

        // Do not show the empty view when the search query is not empty.
        // Done to show the footer for a custom location.
        return viewModel.getSearchValue().isEmpty()
    }

    fun update() {

        // Only show the "default" item when the search query is not empty.
        if (viewModel.getSearchValue().isEmpty()
            || viewModel.getLocations().status == QueryStatus.LOADING
        ) {
            defaultItemView.location_item_default.visibility = View.GONE
        } else {
            defaultItemView.location_item_default.visibility = View.VISIBLE
        }

        // Update the text of the default item.
        defaultItemView.location_item_default_text.text = String.format(
            context.resources.getString(
                R.string.add_item_order_default_text,
                viewModel.getSearchValue()
            )
        )

        // Remove the footer and add it again to prevent errors
        listView.removeFooterView(defaultItemView)
        listView.addFooterView(defaultItemView)

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}