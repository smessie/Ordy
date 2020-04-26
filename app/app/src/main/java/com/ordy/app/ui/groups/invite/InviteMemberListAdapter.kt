package com.ordy.app.ui.groups.invite

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.ordy.app.R
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import kotlinx.android.synthetic.main.list_group_member_card.view.member_name
import kotlinx.android.synthetic.main.list_invite_member_card.view.*
import okhttp3.ResponseBody


class InviteMemberListAdapter(
    val context: Context,
    val activity: InviteMemberActivity,
    val viewModel: InviteMemberViewModel,
    val handlers: InviteMemberHandlers
) :
    BaseAdapter() {

    private val successColor = ColorStateList.valueOf(Color.parseColor("#2ecc71"))
    private val loadingColor = ColorStateList.valueOf(Color.parseColor("#3498db"))
    private val defaultColor = ColorStateList.valueOf(
        ContextCompat.getColor(context, R.color.colorPrimary)
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_invite_member_card,
            parent,
            false
        )

        when (viewModel.getUsers().status) {

            QueryStatus.SUCCESS -> {
                val member = viewModel.getUsers().requireData()[position]

                // Assign the data
                view.member_name.text = member.username

                val inviteResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

                // Set click handler on remove button
                view.member_invite.setOnClickListener {

                    // Invite the user
                    handlers.onInviteButtonClick(inviteResult, member.id)
                }

                // Watch the invite result.
                inviteResult.observe(activity, Observer {

                    when (it.status) {

                        QueryStatus.LOADING -> {
                            view.member_invite.backgroundTintList = loadingColor
                            view.member_invite.text = context.getString(R.string.loading)
                        }

                        QueryStatus.SUCCESS -> {
                            val text = context.getString(R.string.invited_button)

                            view.member_invite.backgroundTintList = successColor
                            view.member_invite.text = text
                        }

                        QueryStatus.ERROR -> {
                            // Handle error
                            ErrorHandler().handle(it.error, view)

                            val text = context.getString(R.string.invite_button)

                            view.member_invite.backgroundTintList = defaultColor
                            view.member_invite.text = text
                        }

                        else -> {
                        }
                    }
                })
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
        return when (viewModel.getUsers().status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> viewModel.getUsers().requireData().size
            else -> 0
        }
    }

}