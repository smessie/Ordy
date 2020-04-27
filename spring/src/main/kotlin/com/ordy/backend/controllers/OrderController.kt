package com.ordy.backend.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.ordy.backend.database.View
import com.ordy.backend.database.models.Order
import com.ordy.backend.services.OrderService
import com.ordy.backend.wrappers.OrderCreateWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/orders")
class OrderController(@Autowired val orderService: OrderService) {

    @PostMapping()
    @JsonView(View.Detail::class)
    fun postOrder(@RequestAttribute userId: Int, @RequestBody orderCreate: OrderCreateWrapper): Order {
        return orderService.createOrder(userId, orderCreate)
    }

    @GetMapping("/{orderId}")
    @JsonView(View.Detail::class)
    fun getOrderById(@RequestAttribute userId: Int, @PathVariable orderId: Int): Order {
        return orderService.getOrder(userId, orderId)
    }

    @PostMapping("/{orderId}/bill")
    fun postBill(@RequestAttribute userId: Int, @PathVariable orderId: Int, @RequestBody image: MultipartFile, request: HttpServletRequest, response: HttpServletResponse) {
        orderService.uploadBillImage(userId, orderId, image)
    }

    @GetMapping("/{orderId}/bill")
    fun getBill(@RequestAttribute userId: Int, @PathVariable orderId: Int, request: HttpServletRequest, response: HttpServletResponse) {
        orderService.getBillImage(userId, orderId, request, response)
    }
}