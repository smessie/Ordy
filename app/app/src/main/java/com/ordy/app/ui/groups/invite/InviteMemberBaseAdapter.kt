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
import com.ordy.app.api.wrappers.GroupInviteUserWrapper
import kotlinx.android.synthetic.main.activity_invite_member.view.*
import kotlinx.android.synthetic.main.list_group_member_card.view.member_name
import kotlinx.android.synthetic.main.list_invite_member_card.view.*
import okhttp3.ResponseBody
import java.util.*


class InviteMemberBaseAdapter(
    val context: Context,
    val activity: InviteMemberActivity,
    val viewModel: InviteMemberViewModel,
    val handlers: InviteMemberHandlers,
    val view: View
) :
    BaseAdapter() {

    private val successColor = ColorStateList.valueOf(Color.parseColor("#2ecc71"))
    private val loadingColor = ColorStateList.valueOf(Color.parseColor("#3498db"))
    private val defaultColor = ColorStateList.valueOf(
        ContextCompat.getColor(context, R.color.colorPrimary)
    )
    private var users: Query<List<GroupInviteUserWrapper>> = Query()

    init {
        val listView = view.users
        val listViewEmpty = view.users_empty
        val searchLoading = view.username_search_loading

        // Watch changes to the "users"
        viewModel.getInviteableUsersMLD().observe(activity, Observer {

            // Show a loading indicator in the searchbox.
            // Hide the list view while loading.
            when (it.status) {

                QueryStatus.LOADING -> {
                    searchLoading.visibility = View.VISIBLE
                    listView.emptyView = null
                }

                QueryStatus.SUCCESS -> {
                    searchLoading.visibility = View.INVISIBLE
                    listView.emptyView = listViewEmpty
                }

                QueryStatus.ERROR -> {
                    searchLoading.visibility = View.INVISIBLE

                    ErrorHandler().handle(it.error, activity)
                }

                else -> {
                }
            }

            update(it)
        })
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_invite_member_card,
            parent,
            false
        )

        when (users.status) {

            QueryStatus.SUCCESS -> {
                val member = users.requireData()
                    .sortedBy { it.user.username.toLowerCase(Locale.getDefault()) }[position]

                // add already invited users to the local list that contains all already invited users
                if (member.invited && !viewModel.isUserInvited(member.user.id)) {
                    viewModel.markUserAsInvited(member.user.id)
                }

                val inviteResult: MutableLiveData<Query<ResponseBody>> = MutableLiveData(Query())

                // Assign the data
                view.member_name.text = member.user.username

                // if the user is already invited to the group
                if (viewModel.isUserInvited(member.user.id)) {
                    setButtonAlreadyInvited(view, inviteResult, member)
                } else {
                    setButtonNotInvited(view, inviteResult, member)
                }

                // Watch the invite result.
                inviteResult.observe(activity, Observer {

                    when (it.status) {

                        QueryStatus.LOADING -> {
                            view.member_invite.backgroundTintList = loadingColor
                            view.member_invite.text = context.getString(R.string.loading)
                        }

                        QueryStatus.SUCCESS -> {
                            if (!viewModel.isUserInvited(member.user.id)) {
                                setButtonAlreadyInvited(view, inviteResult, member)

                                // add this user in the local list that holds all already invited users
                                viewModel.markUserAsInvited(member.user.id)
                            } else {
                                setButtonNotInvited(view, inviteResult, member)

                                // the invite is canceled.
                                viewModel.cancelUserInvite(member.user.id)
                            }

                        }

                        QueryStatus.ERROR -> {
                            // Handle error
                            ErrorHandler().handle(it.error, activity)

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

    fun update(users: Query<List<GroupInviteUserWrapper>>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return when (users.status) {
            QueryStatus.LOADING -> 0
            QueryStatus.SUCCESS -> users.requireData().size
            else -> 0
        }
    }

    /**
     * function to set the invite button of a user who is already invited to this group
     */

    private fun setButtonAlreadyInvited(
        view: View,
        inviteResult: MutableLiveData<Query<ResponseBody>>,
        member: GroupInviteUserWrapper
    ) {

        view.member_invite.text = context.getString(R.string.invited_button)
        view.member_invite.backgroundTintList = successColor

        // setting up the listener for the button
        view.member_invite.setOnClickListener {

            // cancel the invite for this user
            handlers.onDeleteButtonClick(inviteResult, member.user.id)
        }
    }

    /**
     * function to set the invite button of a user who is NOT invited to this group
     */

    private fun setButtonNotInvited(
        view: View,
        inviteResult: MutableLiveData<Query<ResponseBody>>,
        member: GroupInviteUserWrapper
    ) {

        view.member_invite.text = context.getString(R.string.invite_button)
        view.member_invite.backgroundTintList = defaultColor

        // setting up the listener for the button
        view.member_invite.setOnClickListener {

            // Invite the user
            handlers.onInviteButtonClick(inviteResult, member.user.id)
        }
    }
}