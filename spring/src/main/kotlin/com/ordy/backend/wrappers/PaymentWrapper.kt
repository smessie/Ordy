package com.ordy.backend.wrappers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.OrderItem
import com.ordy.backend.database.models.User

class PaymentWrapper(
        @JsonView(View.List::class)
        val user: User,
        @JsonView(View.List::class)
        val order: Order,
        @JsonView(View.List::class)
        val orderItems: Set<OrderItem>
)