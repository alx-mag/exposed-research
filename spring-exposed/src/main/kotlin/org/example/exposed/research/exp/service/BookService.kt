package org.example.exposed.research.exp.service

import org.example.exposed.research.dto.BookResponse
import org.example.exposed.research.dto.CreateBookRequest
import org.example.exposed.research.exp.entity.Book
import org.example.exposed.research.exp.mapper.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService {

    @Transactional
    fun create(request: CreateBookRequest): BookResponse {
        return Book.new {
            title = request.title
            author = request.author
            isbn = request.isbn
            year = request.year
        }.toResponse()
    }
}
