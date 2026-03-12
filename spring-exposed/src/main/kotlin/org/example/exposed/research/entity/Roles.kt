package org.example.exposed.research.entity

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

// Section 4.6 — Roles table, UserRoles join table, Role entity
object Roles : IntIdTable() {
    val name = varchar("name", 100)
}

object UserRoles : Table() {
    val user = reference("user_id", Users)
    val role = reference("role_id", Roles)
    override val primaryKey = PrimaryKey(user, role)
}

class Role(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Role>(Roles)

    var name by Roles.name
    var users by User via UserRoles
}
