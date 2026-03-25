package org.example.exposed.research.exp.jpa.mapper

import org.example.exposed.research.dto.BookResponse
import org.example.exposed.research.dto.OrderResponse
import org.example.exposed.research.exp.jpa.entity.Book
import org.example.exposed.research.exp.jpa.entity.Order

fun Book.toResponse() = BookResponse(requireNotNull(id), title, author, isbn, year)

fun Order.toResponse() = OrderResponse(
    id = requireNotNull(id),
    userId = requireNotNull(user?.id),
    bookId = requireNotNull(book?.id),
    quantity = quantity
)
