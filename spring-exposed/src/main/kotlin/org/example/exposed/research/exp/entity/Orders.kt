package org.example.exposed.research.exp.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Orders : IntIdTable(name = "orders", columnName = "order_id") {
    val user = reference("user_id", Users)
    val book = reference("book_id", Books)
    val quantity = integer("quantity")
}

class Order(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Order>(Orders)

    var user by User referencedOn Orders.user
    var book by Book referencedOn Orders.book
    var quantity by Orders.quantity
}
