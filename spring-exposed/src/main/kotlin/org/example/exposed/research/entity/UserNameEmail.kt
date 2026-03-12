package org.example.exposed.research.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

// Section 4.7 — projection entity mapped to the same Users table
class UserNameEmail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserNameEmail>(Users)

    val name  by Users.name
    val email by Users.email
}
