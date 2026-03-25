package org.example.exposed.research.exp.service

import org.example.exposed.research.dto.CreateOrderRequest
import org.example.exposed.research.dto.OrderResponse
import org.example.exposed.research.exp.entity.Book
import org.example.exposed.research.exp.entity.Order
import org.example.exposed.research.exp.entity.User
import org.example.exposed.research.exp.mapper.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService {

    @Transactional
    fun create(request: CreateOrderRequest): OrderResponse {
        val user = User.findById(request.userId)
            ?: throw NoSuchElementException("User ${request.userId} not found")
        val book = Book.findById(request.bookId)
            ?: throw NoSuchElementException("Book ${request.bookId} not found")

        return Order.new {
            this.user = user
            this.book = book
            quantity = request.quantity
        }.toResponse()
    }
}
