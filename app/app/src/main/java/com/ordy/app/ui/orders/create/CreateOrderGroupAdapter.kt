package com.ordy.app.ui.orders.create

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_item.view.*

class CreateOrderGroupAdapter(
    context: Context,
    val viewModel: CreateOrderViewModel
) : ArrayAdapter<GroupInput>(context, 0) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        when (viewModel.getGroups().status) {

            QueryStatus.LOADING -> {
                view.list_item_text.text = context.getString(R.string.loading)
            }

            QueryStatus.SUCCESS -> {
                val group = viewModel.getGroups().requireData()[position]

                view.list_item_text.text = group.name
            }

            else -> {
            }
        }

        return view
    }

    override fun getItem(position: Int): GroupInput {
        return GroupInput(viewModel.getGroups().requireData()[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return when (viewModel.getGroups().status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> viewModel.getGroups().requireData().size
            else -> 0
        }
    }
}