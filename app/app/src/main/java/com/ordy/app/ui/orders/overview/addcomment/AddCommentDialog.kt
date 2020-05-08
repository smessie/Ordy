package com.ordy.app.ui.orders.overview.addcomment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.MutableLiveData
import com.ordy.app.R
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.Query
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import com.ordy.app.util.InputUtil
import kotlinx.android.synthetic.main.dialog_order_item_comment.view.*
import okhttp3.ResponseBody

class AddCommentDialog(
    val order: Order,
    val orderItem: OrderItem,
    val updateResult: MutableLiveData<Query<ResponseBody>>,
    val viewModel: OverviewOrderViewModel
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_order_item_comment, null)

        val commentView = view.order_item_comment

        if (commentView.editText != null) {
            // Initial text for the comment view
            commentView.editText?.text = SpannableStringBuilder(orderItem.comment)
        }

        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Edit comment of item")
            setMessage("Enter the extra comment for '${orderItem.item.name}'")
            setView(view)
            setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                val comment = InputUtil.extractText(view.order_item_comment)

                // Update the item
                viewModel.updateItem(
                    updateResult,
                    order.id,
                    orderItem.id,
                    comment
                )
            }
            setNegativeButton(android.R.string.cancel) { dialog: DialogInterface?, which: Int ->
                // Do nothing
            }
        }.create()
    }
}