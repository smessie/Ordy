package com.ordy.app.util.types

import com.ordy.app.api.models.OrderItem

data class OrderItemGroup(
    var name: String,
    var quantity: Int,
    var items: MutableList<OrderItem>
)