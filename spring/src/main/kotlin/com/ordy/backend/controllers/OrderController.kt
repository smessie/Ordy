package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Order
import com.ordy.backend.database.models.User
import com.ordy.backend.services.GroupService
import com.ordy.backend.services.OrderService
import com.ordy.backend.wrappers.OrderCreateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(@Autowired val orderService: OrderService) {

    @PostMapping()
    @JsonView(View.Detail::class)
    fun postOrder(@RequestAttribute user: User, @RequestBody orderCreate: OrderCreateWrapper): Order {
        return orderService.createOrder(user, orderCreate)
    }

    @GetMapping("/{orderId}")
    @JsonView(View.Detail::class)
    fun getOrderById(@RequestAttribute user: User, @PathVariable orderId: Int): Order {
        return orderService.getOrder(user, orderId)
    }

    @PostMapping("/{orderId}/bill")
    fun postBill(@PathVariable orderId: Int) {
        // TODO
    }

    @GetMapping("/{orderId}/bill")
    fun getBill(@PathVariable orderId: Int) {
        // TODO
    }
}