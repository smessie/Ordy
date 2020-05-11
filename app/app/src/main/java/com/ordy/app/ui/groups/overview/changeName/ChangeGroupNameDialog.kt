package com.ordy.app.ui.groups.overview.changeName

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.api.util.InputField
import com.ordy.app.api.util.QueryStatus
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import com.ordy.app.util.InputUtil
import com.ordy.app.util.SnackbarUtil
import com.ordy.app.util.types.SnackbarType
import kotlinx.android.synthetic.main.dialog_change_group_name.view.*


class ChangeGroupNameDialog : AppCompatDialogFragment() {

    private lateinit var viewModel: OverviewGroupViewModel
    private lateinit var activity: OverviewGroupActivity

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (this.arguments == null) {
            throw Exception("Something failed")
        }

        val arguments: ChangeGroupNameDialogArgumentsWrapper =
            arguments?.getSerializable("args") as ChangeGroupNameDialogArgumentsWrapper

        activity = arguments.activity
        viewModel = arguments.viewModel

        val group: Group = viewModel.getGroup().requireData()

        val dialogView =
            View.inflate(context, R.layout.dialog_change_group_name, null)

        val newNameView: TextInputLayout = dialogView.new_group_name

        if (newNameView.editText != null) {
            // Initial text for the "New name"-field view
            newNameView.editText?.text = SpannableStringBuilder(group.name)
        }

        val dialog = AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.group_rename_dialog_title)
            setMessage(R.string.group_rename_dialog_message)
            setView(dialogView)
            setPositiveButton(android.R.string.ok, null)
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                // Do nothing
            }
        }.create()


        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {

                val newName: String = InputUtil.extractText(newNameView)

                if (newName != group.name) {

                    viewModel.renameGroup(group.id, newName)

                    // Observe the rename group query
                    viewModel.getRenameGroupMLD().observe(this, Observer {

                        when (it.status) {

                            QueryStatus.SUCCESS -> {
                                SnackbarUtil.openSnackbar(
                                    getString(R.string.group_rename_successful),
                                    activity,
                                    Snackbar.LENGTH_SHORT,
                                    SnackbarType.SUCCESS
                                )

                                // Dismiss once everything is OK.
                                dialog.dismiss()

                                // Refresh the group because group has new name
                                viewModel.refreshGroup(viewModel.getGroup().requireData().id)

                                // This is needed because otherwise the next time when the positive button is clicked will also show a success snackBar.
                                it.status = QueryStatus.INITIALIZED
                            }

                            QueryStatus.ERROR -> {
                                ErrorHandler().handle(
                                    it.error,
                                    activity,
                                    listOf(
                                        InputField(
                                            "name",
                                            dialogView.findViewById(R.id.new_group_name)
                                        )
                                    )
                                )
                            }

                            else -> {
                                // Do nothing
                            }
                        }
                    })
                } else { // When the name is not changed, just close the AlertDialog
                    dialog.dismiss()
                }
            }
        }

        return dialog
    }
}