package com.ordy.app.ui.groups

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import kotlinx.android.synthetic.main.list_group_card.view.*

class GroupsListAdapter(val context: Context?, var groups: Query<List<Group>>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_group_card,
            parent,
            false
        )

        when (groups.status) {

            QueryStatus.LOADING -> {
                /* TODO: make loading effect */
            }

            QueryStatus.SUCCESS -> {
                val group = groups.requireData()[position]

                /* TODO: stop loading effect */

                // assinging data
                /* TODO: meer velden / andere velden*/
                view.group_name.text = group.name
                view.group_creator.text = group.creator.username

                // click handler
                view.group.setOnClickListener {
                    val intent = Intent(view.context, OverviewGroupActivity::class.java)

                    // Pass the group as extra information
                    intent.putExtra("group_id", group.id)

                    view.context.startActivity(intent)
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
        return when (groups.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> groups.requireData().size
            else -> 0
        }
    }
}
