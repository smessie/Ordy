package com.ordy.app.ui.orders.overview.addcomment

import androidx.lifecycle.MutableLiveData
import com.ordy.app.api.models.Order
import com.ordy.app.api.models.OrderItem
import com.ordy.app.api.util.Query
import com.ordy.app.ui.orders.overview.OverviewOrderViewModel
import okhttp3.ResponseBody
import java.io.Serializable

class AddCommentDialogArgumentsWrapper (
    val order: Order,
    val orderItem: OrderItem,
    val updateResult: MutableLiveData<Query<ResponseBody>>,
    val viewModel: OverviewOrderViewModel
): Serializable