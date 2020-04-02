package com.ordy.app.ui.groups.overview

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.AppPreferences
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_group_member_card.view.*

class OverviewGroupListAdapter(
    val context: Context?,
    var viewModel: OverviewGroupViewModel,
    val handlers: OverviewGroupHandlers,
    val activity: OverviewGroupActivity
) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_group_member_card,
            parent,
            false
        )

        when (viewModel.getGroup().status) {
            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.member_loading.startShimmer()
                view.member_loading.visibility = View.VISIBLE
                view.member_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val member = viewModel.getGroup().requireData().members[position]

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
                        setMessage("You are about to remove " + member.username + " from this group")

                        setPositiveButton(android.R.string.ok) { _, _ ->
                            handlers.removeMember(viewModel.getGroup().requireData().id, member.id)
                        }

                        setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.cancel()
                        }
                    }.show()
                }

                // Hide the remove button if the member is the the creator
                if (member.id == viewModel.group.value!!.requireData().creator.id) {
                    view.member_remove.visibility = View.INVISIBLE
                }

                // Hide the remove button for yourself
                if (member.id == AppPreferences(context!!).userId) {
                    view.member_remove.visibility = View.INVISIBLE
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
        return when (viewModel.getGroup().status) {
            QueryStatus.LOADING -> 6
            QueryStatus.SUCCESS -> viewModel.getGroup().requireData().members.size
            else -> 0
        }
    }

}