package com.ordy.app.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.ordy.app.R
import com.ordy.app.api.models.actions.enums.InviteActionOptions
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.SnackbarType
import com.ordy.app.util.SnackbarUtil
import kotlinx.android.synthetic.main.list_group_card.view.group_creator
import kotlinx.android.synthetic.main.list_group_card.view.group_data
import kotlinx.android.synthetic.main.list_group_card.view.group_loading
import kotlinx.android.synthetic.main.list_group_card.view.group_member_count
import kotlinx.android.synthetic.main.list_group_card.view.group_name
import kotlinx.android.synthetic.main.list_invite_group_card.view.*
import okhttp3.ResponseBody

class InvitesListAdapter(
    val context: Context?,
    val viewModel: ProfileViewModel,
    val activity: ProfileActivity,
    val handlers: ProfileHandlers
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_invite_group_card,
            parent,
            false
        )

        when (viewModel.getInvites().status) {
            QueryStatus.LOADING -> {
                // Start the shimmer effect & show
                view.group_loading.startShimmer()
                view.group_loading.visibility = View.VISIBLE
                view.group_data.visibility = View.GONE
            }

            QueryStatus.SUCCESS -> {
                val group = viewModel.getInvites().requireData()[position]

                // Stop the shimmer effect & hide.
                view.group_loading.stopShimmer()
                view.group_loading.visibility = View.GONE
                view.group_data.visibility = View.VISIBLE

                // Assign data
                view.group_name.text = group.name
                view.group_creator.text = group.creator.username
                view.group_member_count.text = "${group.membersCount}"

                val actionInviteResult: MutableLiveData<Query<ResponseBody>> =
                    MutableLiveData(Query())
                var acceptRequest = false

                view.accept_invite.setOnClickListener {
                    acceptRequest = true
                    handlers.handleClickInviteAction(
                        InviteActionOptions.ACCEPT,
                        group.id,
                        actionInviteResult
                    )
                }

                view.decline_invite.setOnClickListener {
                    acceptRequest = false
                    handlers.handleClickInviteAction(
                        InviteActionOptions.DENY,
                        group.id,
                        actionInviteResult
                    )
                }

                actionInviteResult.observe(activity, Observer {
                    when (it.status) {
                        QueryStatus.SUCCESS -> {
                            var message = ""
                            message = if (acceptRequest) {
                                "Successfully accepted group invite."
                            } else {
                                "Successfully declined group invite."
                            }
                            viewModel.refreshInvites()
                            SnackbarUtil.openSnackbar(
                                message,
                                view,
                                Snackbar.LENGTH_LONG,
                                SnackbarType.SUCCESS
                            )
                        }

                        QueryStatus.ERROR -> {
                            ErrorHandler.handle(it.error, view, listOf())
                        }
                    }
                })
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
        return when (viewModel.getInvites().status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> viewModel.getInvites().requireData().size
            else -> 0
        }
    }
}