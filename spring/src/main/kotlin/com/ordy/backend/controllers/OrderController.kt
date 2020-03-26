package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController {
    @PostMapping("/")
    fun postOrder() {

    }

    @GetMapping("/{orderId}")
    fun getOrderById(@PathVariable orderId: Int) {
        //TODO: also return: orderedItems: [ { ... }, { ... }, ... , { ... }]
    }

    @PostMapping("/{orderId}/bill")
    fun postBill(@PathVariable orderId: Int) {

    }

    @GetMapping("/{orderId}/bill")
    fun getBill(@PathVariable orderId: Int) {

    }
}