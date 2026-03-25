package org.example.exposed.research.exp.jpa.service

import org.example.exposed.research.dto.CreateBookRequest
import org.example.exposed.research.exp.jpa.entity.Book
import org.example.exposed.research.exp.jpa.repo.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(private val books: BookRepository) {

    fun create(request: CreateBookRequest): Book {
        val book = Book(
            title = request.title,
            author = request.author,
            isbn = request.isbn,
            year = request.year
        )
        return books.save(book)
    }
}
