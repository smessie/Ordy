package com.ordy.app.ui.groups.invite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.ordy.app.R
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_group_member_card.view.member_data
import kotlinx.android.synthetic.main.list_group_member_card.view.member_email
import kotlinx.android.synthetic.main.list_group_member_card.view.member_loading
import kotlinx.android.synthetic.main.list_group_member_card.view.member_name
import kotlinx.android.synthetic.main.list_invite_member_card.view.*

class InviteMemberListAdapter(
    val context: Context?,
    var viewModel: InviteMemberViewModel,
    val handlers: InviteMemberHandlers
) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_invite_member_card,
            parent,
            false
        )

        when (viewModel.getUsers().status) {
            QueryStatus.LOADING -> {

                // Start the shimmer effect & show
                view.member_loading.startShimmer()
                view.member_loading.visibility = View.VISIBLE
                view.member_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val member = viewModel.getUsers().requireData()[position]

                // Stop the shimmer effect & hide
                view.member_loading.stopShimmer()
                view.member_loading.visibility = View.GONE
                view.member_data.visibility = View.VISIBLE

                // Assign the data
                view.member_name.text = member.username
                view.member_email.text = member.email

                // Set click handler on remove button
                view.member_invite.setOnClickListener {
                    handlers.onInviteButtonClick(member.id)
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
        return when (viewModel.getUsers().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> viewModel.getUsers().requireData().size
            else -> 0
        }
    }

}