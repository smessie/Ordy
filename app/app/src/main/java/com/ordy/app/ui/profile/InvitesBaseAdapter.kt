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
import com.ordy.app.api.models.GroupInvite
import com.ordy.app.api.models.actions.enums.InviteActionOptions
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.Query
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.util.SnackbarUtil
import com.ordy.app.util.types.SnackbarType
import kotlinx.android.synthetic.main.activity_profile.view.*
import kotlinx.android.synthetic.main.list_invite_group_card.view.*
import okhttp3.ResponseBody
import java.util.*

class InvitesBaseAdapter(
    val context: Context?,
    val viewModel: ProfileViewModel,
    val activity: ProfileActivity,
    val handlers: ProfileHandlers,
    val view: View
) : BaseAdapter() {

    private var invites: Query<List<GroupInvite>> = Query()

    init {
        viewModel.getInvitesMLD().observe(activity, Observer {
            // Stop refreshing on load
            if (it.status == QueryStatus.SUCCESS || it.status == QueryStatus.ERROR) {
                view.group_invites_refresh.isRefreshing = false
            }

            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_invite_group_card,
            parent,
            false
        )

        when (invites.status) {
            QueryStatus.LOADING -> {
                // Start the shimmer effect & show
                view.group_loading.startShimmer()
                view.group_loading.visibility = View.VISIBLE
                view.group_data.visibility = View.INVISIBLE
            }

            QueryStatus.SUCCESS -> {
                val invite = invites.requireData()
                    .sortedBy { it.group.name.toLowerCase(Locale.getDefault()) }[position]

                // Stop the shimmer effect & hide.
                view.group_loading.stopShimmer()
                view.group_loading.visibility = View.GONE
                view.group_data.visibility = View.VISIBLE

                // Assign data
                view.group_name.text = invite.group.name
                view.group_creator.text = invite.group.creator.username
                view.group_member_count.text = "${invite.group.membersCount}"

                val actionInviteResult: MutableLiveData<Query<ResponseBody>> =
                    MutableLiveData(Query())
                var acceptRequest = false

                view.accept_invite.setOnClickListener {
                    acceptRequest = true
                    handlers.handleClickInviteAction(
                        InviteActionOptions.ACCEPT,
                        invite.group.id,
                        actionInviteResult
                    )
                }

                view.decline_invite.setOnClickListener {
                    acceptRequest = false
                    handlers.handleClickInviteAction(
                        InviteActionOptions.DENY,
                        invite.group.id,
                        actionInviteResult
                    )
                }

                actionInviteResult.observe(activity, Observer {
                    when (it.status) {
                        QueryStatus.SUCCESS -> {
                            val message: String = if (acceptRequest) {
                                "Successfully accepted group invite."
                            } else {
                                "Successfully declined group invite."
                            }

                            viewModel.refreshInvites()

                            SnackbarUtil.openSnackbar(
                                message,
                                activity,
                                Snackbar.LENGTH_LONG,
                                SnackbarType.SUCCESS
                            )
                        }

                        QueryStatus.ERROR -> {
                            ErrorHandler().handle(it.error, activity, listOf())
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

    fun update(invites: Query<List<GroupInvite>>) {
        this.invites = invites
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return when (invites.status) {
            QueryStatus.LOADING -> 4
            QueryStatus.SUCCESS -> invites.requireData().size
            else -> 0
        }
    }
}