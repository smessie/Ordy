package com.ordy.app.util.types

import com.ordy.app.api.models.OrderItem

data class OrderItemUserGroup(
    var username: String,
    var items: MutableList<OrderItem>
)