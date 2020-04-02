package com.ordy.backend.controllers

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/payments")
class PaymentController {
    @GetMapping("/debtors")
    fun getDebtors() {

    }

    @GetMapping("/debts")
    fun getDebts() {

    }

    @PatchMapping("/{orderId}/{userId}")
    fun patchOrderPayed(@PathVariable orderId: Int, @PathVariable userId: Int) {

    }

    @PatchMapping("/{orderId}/{userId}/notification")
    fun postDebtNotification(@PathVariable orderId: Int, @PathVariable userId: Int) {

    }
}