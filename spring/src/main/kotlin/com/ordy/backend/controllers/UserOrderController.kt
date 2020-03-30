package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.OrderItem
import com.ordy.backend.services.OrderService
import com.ordy.backend.wrappers.OrderAddItemWrapper
import com.ordy.backend.wrappers.OrderUpdateItemWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user/orders")
class UserOrderController(@Autowired val orderService: OrderService) {

    @GetMapping
    @JsonView(View.List::class)
    fun getOrders(@RequestAttribute userId: Int): List<Order> {
        return orderService.getOrders(userId)
    }

    @PatchMapping("/{orderId}")
    fun patchUserOrder(@PathVariable orderId: Int) {

    }

    @PostMapping("/{orderId}/items")
    @JsonView(View.Detail::class)
    fun postOrderItem(
            @RequestAttribute userId: Int,
            @PathVariable orderId: Int,
            @RequestBody orderAddItem: OrderAddItemWrapper
    ): OrderItem {
        return orderService.addItemOrder(userId, orderId, orderAddItem)
    }

    @PatchMapping("/{orderId}/items/{orderItemId}")
    fun patchOrderItem(
            @RequestAttribute userId: Int,
            @PathVariable orderId: Int,
            @PathVariable orderItemId: Int,
            @RequestBody orderUpdateItem: OrderUpdateItemWrapper
    ) {
        orderService.updateItemOrder(userId, orderId, orderItemId, orderUpdateItem)
    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    fun deleteOrderItem(@PathVariable orderId: Int, @PathVariable orderItemId: Int) {

    }
}