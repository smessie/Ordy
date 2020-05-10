package com.ordy.app.ui.groups.overview.changeName

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.ui.groups.overview.OverviewGroupActivity
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.dialog_change_group_name.view.*


class ChangeGroupNameDialog(
    val viewModel: OverviewGroupViewModel,
    val activity: OverviewGroupActivity
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val group: Group = viewModel.getGroup().requireData()

        val dialogView =
            View.inflate(context, R.layout.dialog_change_group_name, null)

        val newNameView: TextInputLayout = dialogView.new_group_name

        if (newNameView.editText != null) {
            // Initial text for the "New name"-field view
            newNameView.editText?.text = SpannableStringBuilder(group.name)
        }

        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.group_rename_dialog_title)
            setMessage(R.string.group_rename_dialog_message)
            setView(dialogView)
            setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                val newName: String = InputUtil.extractText(newNameView)
                if (!newName.isBlank()) {
                    // Update group name
                    viewModel.renameGroup(group.id, newName)
                } else {
                    ErrorHandler().handleRawGeneral(
                        getString(R.string.group_rename_dialog_is_empty),
                        activity
                    )
                }
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                // Do nothing
            }
        }.create()
    }
}