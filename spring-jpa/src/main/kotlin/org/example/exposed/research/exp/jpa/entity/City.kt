package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

// Section 4.1, 4.6 — JPA City entity with One-to-Many relationship to JpaUser
@Entity
@Table(name = "cities")
class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var name: String = "",

    @OneToMany(mappedBy = "city")
    var users: MutableList<User> = mutableListOf()
)