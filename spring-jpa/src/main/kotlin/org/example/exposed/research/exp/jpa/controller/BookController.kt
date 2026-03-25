package org.example.exposed.research.exp.jpa.controller

import org.example.exposed.research.dto.BookResponse
import org.example.exposed.research.dto.CreateBookRequest
import org.example.exposed.research.exp.jpa.mapper.toResponse
import org.example.exposed.research.exp.jpa.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(private val books: BookService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createBook(@RequestBody request: CreateBookRequest): BookResponse {
        return books.create(request).toResponse()
    }
}
