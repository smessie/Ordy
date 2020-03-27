package com.ordy.app.ui.groups

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_group_card.view.*

class GroupsListAdapter (val context: Context?, var groups: Query<List<Group>>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_group_card, parent, false)

        when (groups.status) {

            QueryStatus.LOADING -> {
                /* TODO: make loading effect */
            }

            QueryStatus.SUCCESS -> {
                val group = groups.requireData()[position]

                /* TODO: stop loading effect */

                // assinging data
                /* TODO: meer dan alleen dit veld*/
                view.group_name.text = group.name

                // click handler
                /* TODO */
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
            QueryStatus.LOADING -> 4 // TODO VRAAG: waarom 4?
            QueryStatus.SUCCESS -> groups.requireData().size
            else -> 0
        }
    }
}
