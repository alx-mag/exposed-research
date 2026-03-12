package org.example.exposed.research.jpa

import jakarta.persistence.*

// Section 4.1, 4.6 — JPA City entity with One-to-Many relationship to JpaUser
@Entity
@Table(name = "cities")
class City(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var name: String = "",

    @OneToMany(mappedBy = "city")
    var users: MutableList<JpaUser> = mutableListOf()
)
