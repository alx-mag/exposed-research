package org.example.exposed.research.exp.entity

import org.example.utils.org.example.exposed.research.dto.UserDto
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

// Section 4.1, 4.6 — Users table with optional FK columns so existing code compiles unchanged
object Users : IntIdTable() {
    val name = varchar("name", 255)
    val email = varchar("email", 255).default("")
    val age = integer("age").default(0)
    val city = reference("city_id", Cities).nullable()
    val profile = reference("profile_id", Profiles).nullable()
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var age by Users.age
    var city by City optionalReferencedOn Users.city      // Many-to-One (nullable)
    var profile by Profile optionalReferencedOn Users.profile // One-to-One (nullable)
    var roles by Role via UserRoles                         // Many-to-Many

    fun toResponse() = UserDto(id.value, name, age)
}
