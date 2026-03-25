package org.example.exposed.research.dto

data class CreateBookRequest(
    val title: String,
    val author: String,
    val isbn: String,
    val year: Int
)

data class BookResponse(
    val id: Int,
    val title: String,
    val author: String,
    val isbn: String,
    val year: Int
)

data class CreateOrderRequest(
    val userId: Int,
    val bookId: Int,
    val quantity: Int
)

data class OrderResponse(
    val id: Int,
    val userId: Int,
    val bookId: Int,
    val quantity: Int
)
