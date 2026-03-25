package org.example.exposed.research.exp.jpa.service

import org.example.exposed.research.dto.CreateOrderRequest
import org.example.exposed.research.exp.jpa.entity.Order
import org.example.exposed.research.exp.jpa.repo.BookRepository
import org.example.exposed.research.exp.jpa.repo.OrderRepository
import org.example.exposed.research.exp.jpa.repo.UserRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orders: OrderRepository,
    private val users: UserRepository,
    private val books: BookRepository
) {

    fun create(request: CreateOrderRequest): Order {
        val user = users.findById(request.userId)
            .orElseThrow { NoSuchElementException("User ${request.userId} not found") }
        val book = books.findById(request.bookId)
            .orElseThrow { NoSuchElementException("Book ${request.bookId} not found") }
        val order = Order(
            user = user,
            book = book,
            quantity = request.quantity
        )
        return orders.save(order)
    }
}
