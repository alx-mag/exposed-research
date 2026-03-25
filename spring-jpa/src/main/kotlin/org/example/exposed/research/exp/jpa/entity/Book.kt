package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "books")
class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    var id: Int? = null,

    @Column(nullable = false, length = 255)
    var title: String = "",

    @Column(nullable = false, length = 255)
    var author: String = "",

    @Column(nullable = false, length = 255)
    var isbn: String = "",

    @Column(name = "year", nullable = false)
    var year: Int = 0,

    @OneToMany(mappedBy = "book")
    var orders: MutableList<Order> = mutableListOf()
)
