package com.ordy.app.ui.orders.overview.additem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import com.ordy.app.R
import com.ordy.app.api.models.Item
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_order_cuisine_item.view.*
import kotlinx.android.synthetic.main.list_order_cuisine_item_default.view.*

class AddItemOrderListAdapter(
    val activity: AddItemOrderActivity,
    val orderId: Int,
    var cuisine: Query<List<Item>>,
    var searchValue: String
) : BaseAdapter() {

    private var cuisineFiltered: List<Item> = emptyList()

    private val listView: ListView = activity.findViewById(R.id.order_cuisine_items)
    private var defaultItemView = LayoutInflater.from(activity.applicationContext).inflate(R.layout.list_order_cuisine_item_default, null)

    init {
        // Set click handler for default view.
        defaultItemView.add_item_order_default_add.setOnClickListener {
            activity.viewModel.addItem(orderId, null, searchValue)
        }

        listView.addFooterView(defaultItemView)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(activity.applicationContext).inflate(R.layout.list_order_cuisine_item, parent, false)

        when(cuisine.status) {

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
                    activity.viewModel.addItem(orderId, it.id, null)
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
        return when(cuisine.status) {
            QueryStatus.LOADING -> 6
            QueryStatus.SUCCESS -> cuisineFiltered.size
            else -> 0
        }
    }

    fun update() {

        if(cuisine.status == QueryStatus.SUCCESS) {

            // Create a filtered list that complies with the given search result.
            cuisineFiltered = cuisine.requireData().filter { it.name.toLowerCase().matches(Regex(".*${searchValue.toLowerCase()}.*")) }

            // Add the "default" item to the bottom of the listview
            // This item serves as a fallback when no correct matches were found.
            defaultItemView.add_item_order_default_text.text = String.format(
                activity.applicationContext.resources.getString(R.string.add_item_order_default_text, searchValue)
            )

            // Only show the "default" item when the search query is not empty.
            if(searchValue.isEmpty()) {
                listView.removeFooterView(defaultItemView)
            } else {
                // Remove & add since we don't know if it was already added or not
                listView.removeFooterView(defaultItemView)
                listView.addFooterView(defaultItemView)
            }
        }

        // Notify the changes to the list view (to re-render automatically)
        this.notifyDataSetChanged()
    }
}