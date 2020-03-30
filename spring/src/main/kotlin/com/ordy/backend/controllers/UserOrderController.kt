package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.User
import com.ordy.backend.services.OrderService
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
    fun postOrderItem(@PathVariable orderId: Int) {

    }

    @PatchMapping("/{orderId}/items/{orderItemId}")
    fun patchOrderItem(@PathVariable orderId: Int, @PathVariable orderItemId: Int) {

    }

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    fun deleteOrderItem(@PathVariable orderId: Int, @PathVariable orderItemId: Int) {

    }
}