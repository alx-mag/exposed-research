package org.example.exposed.research.exp.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Books : IntIdTable(name = "books", columnName = "book_id") {
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val isbn = varchar("isbn", 255)
    val year = integer("year")
}

class Book(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Book>(Books)

    var title by Books.title
    var author by Books.author
    var isbn by Books.isbn
    var year by Books.year
    val orders by Order referrersOn Orders.book
}
