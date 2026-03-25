package org.example.exposed.research.exp.jpa.repo

import org.example.exposed.research.exp.jpa.entity.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Int>
