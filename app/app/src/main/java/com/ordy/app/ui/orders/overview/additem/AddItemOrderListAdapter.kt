package com.ordy.app.ui.orders.overview.additem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import com.ordy.app.R
import com.ordy.app.api.models.Item
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_order_cuisine_item.view.*
import kotlinx.android.synthetic.main.list_order_cuisine_item_default.view.*

class AddItemOrderListAdapter(
    val activity: AddItemOrderActivity,
    val orderId: Int,
    val viewModel: AddItemOrderViewModel
) : BaseAdapter() {

    private var cuisineFiltered: List<Item> = emptyList()

    private val listView: ListView = activity.findViewById(R.id.order_cuisine_items)
    private var defaultItemView = LayoutInflater.from(activity.applicationContext)
        .inflate(R.layout.list_order_cuisine_item_default, null)

    init {
        // Set click handler for default view.
        defaultItemView.add_item_order_default_add.setOnClickListener {
            if (viewModel.getAddItemResult().status != QueryStatus.LOADING) {
                activity.handlers.addItem(orderId, null, viewModel.getSearchValue())
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(activity.applicationContext)
            .inflate(R.layout.list_order_cuisine_item, parent, false)

        when (viewModel.getCuisineItems().status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.order_cuisine_item_loading.startShimmer()
                view.order_cuisine_item_loading.visibility = View.VISIBLE
                view.order_cuisine_item_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val cuisineItem = cuisineFiltered[position]

                // Stop the shimmer effect & hide.
                view.order_cuisine_item_loading.stopShimmer()
                view.order_cuisine_item_loading.visibility = View.GONE
                view.order_cuisine_item_data.visibility = View.VISIBLE

                // Assign the data.
                view.order_cuisine_item_name.text = cuisineItem.name

                // Set click handler.
                view.order_cuisine_add.setOnClickListener {
                    if (viewModel.getAddItemResult().status != QueryStatus.LOADING) {
                        activity.handlers.addItem(orderId, cuisineItem.id, null)
                    }
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
        return when (viewModel.getCuisineItems().status) {
            QueryStatus.LOADING -> 6
            QueryStatus.SUCCESS -> cuisineFiltered.size
            else -> 0
        }
    }

    fun update() {

        if (viewModel.getCuisineItems().status == QueryStatus.SUCCESS) {

            // Create a filtered list that complies with the given search result.
            cuisineFiltered = viewModel.getCuisineItems().requireData().filter {
                it.name.toLowerCase()
                    .matches(Regex(".*${viewModel.getSearchValue().toLowerCase()}.*"))
            }

            // Add the "default" item to the bottom of the listview
            // This item serves as a fallback when no correct matches were found.
            defaultItemView.add_item_order_default_text.text = String.format(
                activity.applicationContext.resources.getString(
                    R.string.add_item_order_default_text,
                    viewModel.getSearchValue()
                )
            )

            // Remove the footer and add it again to prevent errors
            listView.removeFooterView(defaultItemView)
            listView.addFooterView(defaultItemView)

            // Only show the "default" item when the search query is not empty.
            if (viewModel.getSearchValue().isEmpty()) {
                defaultItemView.add_item_order_default.visibility = View.GONE
            } else {
                defaultItemView.add_item_order_default.visibility = View.VISIBLE
            }
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}