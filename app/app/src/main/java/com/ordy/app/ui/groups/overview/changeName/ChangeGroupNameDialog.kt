package com.ordy.app.ui.groups.overview.changeName

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.ordy.app.R
import com.ordy.app.api.models.Group
import com.ordy.app.api.util.ErrorHandler
import com.ordy.app.ui.groups.overview.OverviewGroupViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.fragment_groups.view.*


class ChangeGroupNameDialog(
    val viewModel: OverviewGroupViewModel,
    val activityView: View
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val group: Group = viewModel.getGroup().requireData()
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_group_name, null)
        val newNameView: TextInputLayout = dialogView.findViewById(R.id.new_group_name)

        // Initial text for the "New name"-field view
        newNameView.editText!!.text = SpannableStringBuilder(group.name)

        return AlertDialog.Builder(requireContext()).apply {
            setTitle("New group-name")
            setMessage("Enter a new name:")
            setView(dialogView)
            setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                val newName: String = InputUtil.extractText(newNameView)
                if (!newName.isNullOrBlank()) {
                    // Update group name
                    viewModel.renameGroup(group.id, newName)
                } else {
                    ErrorHandler.handleRawGeneral("Name cannot be empty.", activityView)
                }
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                // Do nothing
            }
        }.create()
    }
}