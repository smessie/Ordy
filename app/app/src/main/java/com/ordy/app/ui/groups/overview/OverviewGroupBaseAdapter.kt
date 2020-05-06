package com.ordy.app.ui.groups.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_group_member_card.view.*

class OverviewGroupBaseAdapter(
    val context: Context?,
    val viewModel: OverviewGroupViewModel,
    val handlers: OverviewGroupHandlers,
    val activity: OverviewGroupActivity,
    val view: View
) :
    BaseAdapter() {

    private var group: Query<Group> = Query()

    init {
        viewModel.groupMLD.observe(activity, Observer {
            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_group_member_card,
            parent,
            false
        )

        when (group.status) {
            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.member_loading.startShimmer()
                view.member_loading.visibility = View.VISIBLE
                view.member_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val member = group.requireData().members!![position]

                // Stop the shimmer effect & hide
                view.member_loading.stopShimmer()
                view.member_loading.visibility = View.GONE
                view.member_data.visibility = View.VISIBLE

                // Assign the data
                view.member_name.text = member.username
                view.member_email.text = member.email

                // Set click handler on remove button
                view.member_remove.setOnClickListener {
                    AlertDialog.Builder(activity).apply {
                        setTitle("Are you sure you want to remove this member?")
                        setMessage("You are about to remove ${member.username} from this group")

                        setPositiveButton(android.R.string.ok) { _, _ ->
                            handlers.removeMember(
                                group.requireData().id,
                                member.id
                            )
                        }

                        setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.cancel()
                        }
                    }.show()
                }

                // Hide the remove button if the member is the the creator
                if (member.id == group.requireData().creator.id) {
                    view.member_remove.visibility = View.INVISIBLE
                }

                // Hide the remove button for yourself
                if (member.id == AppPreferences(context!!).userId) {
                    view.member_remove.visibility = View.INVISIBLE
                }
            }

            else -> {
            }
        }

        return view
    }

    fun update(group: Query<Group>) {
        this.group = group
        notifyDataSetChanged()
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return when (group.status) {
            QueryStatus.LOADING -> 6
            QueryStatus.SUCCESS -> group.requireData().members!!.size
            else -> 0
        }
    }

}