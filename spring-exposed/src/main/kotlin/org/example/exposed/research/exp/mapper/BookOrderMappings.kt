package org.example.exposed.research.exp.mapper

import org.example.exposed.research.dto.BookResponse
import org.example.exposed.research.dto.OrderResponse
import org.example.exposed.research.exp.entity.Book
import org.example.exposed.research.exp.entity.Order

fun Book.toResponse() = BookResponse(id.value, title, author, isbn, year)

fun Order.toResponse() = OrderResponse(
    id = id.value,
    userId = user.id.value,
    bookId = book.id.value,
    quantity = quantity
)
