package org.example.exposed.research.exp.controller

import org.example.exposed.research.dto.CreateOrderRequest
import org.example.exposed.research.dto.OrderResponse
import org.example.exposed.research.exp.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orders: OrderService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest): OrderResponse {
        return orders.create(request)
    }
}
