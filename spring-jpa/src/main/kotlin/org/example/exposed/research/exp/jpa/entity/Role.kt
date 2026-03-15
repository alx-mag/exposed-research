package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

// Section 4.6 — JPA Role entity stub (Many-to-Many target)
@Entity
@Table(name = "roles")
class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var name: String = "",

    @ManyToMany(mappedBy = "roles")
    var users: MutableList<User> = mutableListOf()
)