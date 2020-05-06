package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.services.PaymentService
import com.ordy.backend.wrappers.PaymentUpdateWrapper
import com.ordy.backend.wrappers.PaymentWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/payments")
class PaymentController(@Autowired val paymentService: PaymentService) {

    @GetMapping("/debtors")
    @JsonView(View.List::class)
    fun getDebtors(@RequestAttribute userId: Int): List<PaymentWrapper> {
        return paymentService.getDebtors(userId)
    }

    @GetMapping("/debts")
    @JsonView(View.List::class)
    fun getDebts(@RequestAttribute userId: Int): List<PaymentWrapper> {
        return paymentService.getDebts(userId)
    }

    @PatchMapping("/{orderId}/{userPaidId}")
    @JsonView(View.Empty::class)
    fun patchOrderPayed(@PathVariable orderId: Int, @PathVariable userPaidId: Int, @RequestAttribute userId: Int, @RequestBody paymentUpdateWrapper: PaymentUpdateWrapper) {
        paymentService.patchOrderPayed(userId, orderId, userPaidId, paymentUpdateWrapper)
    }

    @PostMapping("/{orderId}/{receiverId}/notification")
    fun postDebtNotification(@PathVariable orderId: Int, @PathVariable receiverId: Int, @RequestAttribute userId: Int) {
        paymentService.reactOnNotify(orderId, receiverId, userId)
    }
}