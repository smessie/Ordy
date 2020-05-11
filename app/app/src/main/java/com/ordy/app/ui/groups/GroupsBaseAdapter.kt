package com.ordy.app.ui.groups

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import kotlinx.android.synthetic.main.fragment_groups.view.*
import kotlinx.android.synthetic.main.list_group_card.view.*
import java.util.*

class GroupsBaseAdapter(
    val context: Context?,
    var viewModel: GroupsViewModel,
    lifecycleOwner: LifecycleOwner,
    val view: View
) : BaseAdapter() {

    private var groups: Query<List<Group>> = Query()

    init {
        viewModel.getGroupsMLD().observe(lifecycleOwner, Observer {
            // Stop refreshing on load
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                view.groups_refresh.isRefreshing = false
            }

            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_group_card,
            parent,
            false
        )

        when (groups.status) {

            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.group_loading.startShimmer()
                view.group_loading.visibility = View.VISIBLE
                view.group_data.visibility = View.INVISIBLE
            }

            QueryStatus.SUCCESS -> {
                val group = groups.requireData()
                    .sortedBy { it.name.toLowerCase(Locale.getDefault()) }[position]

                // Stop the shimmer effect & hide.
                view.group_loading.stopShimmer()
                view.group_loading.visibility = View.GONE
                view.group_data.visibility = View.VISIBLE

                // Assign data
                view.group_name.text = group.name
                view.group_creator.text = group.creator.username
                view.group_member_count.text = "${group.membersCount}"

                // Click handler
                view.setOnClickListener {
                    val intent = Intent(view.context, OverviewGroupActivity::class.java)

                    // Pass the group as extra information
                    intent.putExtra("group_id", group.id)

                    view.context.startActivity(intent)
                }
            }

            else -> {
            }
        }

        return view
    }

    fun update(groups: Query<List<Group>>) {
        this.groups = groups
        notifyDataSetChanged()
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
