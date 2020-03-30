package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user/orders")
class UserOrderController {
    @GetMapping
    fun getOrders() {

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