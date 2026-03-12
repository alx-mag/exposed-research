package org.example.exposed.research.entity

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

// Section 4.6 — Profiles table and Profile entity (One-to-One stub)
object Profiles : IntIdTable() {
    val bio = varchar("bio", 500).default("")
}

class Profile(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Profile>(Profiles)

    var bio by Profiles.bio
}
