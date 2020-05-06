package com.ordy.app.ui.orders.overview.additem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Item
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.activity_add_item_order.*
import kotlinx.android.synthetic.main.list_order_cuisine_item.view.*
import kotlinx.android.synthetic.main.list_order_cuisine_item_default.view.*
import java.util.*

class AddItemOrderBaseAdapter(
    val activity: AddItemOrderActivity,
    private val orderId: Int,
    val viewModel: AddItemOrderViewModel,
    val view: View
) : BaseAdapter() {

    private var cuisineItems: Query<List<Item>> = Query()
    private var cuisineFiltered: List<Item> = emptyList()
    private var addItemResult: Query<OrderItem> = Query()
    private var searchValueData: String = ""

    private val listView = activity.order_cuisine_items
    private var defaultItemView =
        View.inflate(activity.applicationContext, R.layout.list_order_cuisine_item_default, null)

    init {
        // Set click handler for default view.
        defaultItemView.add_item_order_default_add.setOnClickListener {
            if (viewModel.getAddItemResult().status != QueryStatus.LOADING) {
                viewModel.addItem(orderId, null, searchValueData)
            }
        }

        // Update the list adapter when the "cuisine" query updates
        viewModel.getCuisineItemsMLD().observe(activity, Observer {

            // Catch possible errors.
            if (it.status == QueryStatus.ERROR) {
                AlertDialog.Builder(activity).apply {
                    setTitle("Unable to fetch predefined items")
                    setMessage(viewModel.getCuisineItems().requireError().message)
                    setPositiveButton(android.R.string.ok) { _, _ ->

                        // Close the activity
                        activity.finish()
                    }
                }.show()
            }

            // Update the orders
            update(it, searchValueData)
        })

        // Observe the result of adding an item to the order.
        viewModel.getAddItemMLD().observe(activity, Observer {

            when (it.status) {

                QueryStatus.LOADING -> {
                    SnackbarUtil.openSnackbar(
                        "Adding item...",
                        view
                    )
                }

                QueryStatus.SUCCESS -> {
                    SnackbarUtil.closeSnackbar(view)

                    // Go back to the order overview activity.
                    activity.finish()
                }

                QueryStatus.ERROR -> {
                    SnackbarUtil.closeSnackbar(view)

                    ErrorHandler().handle(it.error, view)
                }

                else -> {
                }
            }

            addItemResult = it
        })

        // Update the "search value" of the list adapter when a change is observed
        viewModel.searchValueMLD.observe(activity, Observer {

            // Update the list adapter
            update(cuisineItems, it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(activity.applicationContext)
            .inflate(R.layout.list_order_cuisine_item, parent, false)

        when (cuisineItems.status) {

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
                    if (addItemResult.status != QueryStatus.LOADING) {
                        viewModel.addItem(orderId, cuisineItem.id, null)
                    }
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
        return when (cuisineItems.status) {
            QueryStatus.LOADING -> 6
            QueryStatus.SUCCESS -> cuisineFiltered.size
            else -> 0
        }
    }

    fun update(cuisineItems: Query<List<Item>>, searchValueData: String) {
        this.cuisineItems = cuisineItems

        if (cuisineItems.status == QueryStatus.SUCCESS) {

            // Create a filtered list that complies with the given search result.
            cuisineFiltered = cuisineItems.requireData().filter {
                it.name.toLowerCase(Locale.US)
                    .matches(Regex(".*${searchValueData.toLowerCase(Locale.US)}.*"))
            }.sortedBy { it.name }

            // Add the "default" item to the bottom of the listview
            // This item serves as a fallback when no correct matches were found.
            defaultItemView.add_item_order_default_text.text = String.format(
                activity.resources.getString(
                    R.string.add_item_order_default_text,
                    searchValueData
                )
            )

            // Remove the footer and add it again to prevent errors
            listView.removeFooterView(defaultItemView)
            listView.addFooterView(defaultItemView)

            // Only show the "default" item when the search query is not empty.
            if (searchValueData.isEmpty()) {
                defaultItemView.add_item_order_default.visibility = View.GONE
            } else {
                defaultItemView.add_item_order_default.visibility = View.VISIBLE
            }
        }

        // Notify the changes to the list view (to re-render automatically)
        notifyDataSetChanged()
    }
}