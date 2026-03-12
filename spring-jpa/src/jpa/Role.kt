package org.example.exposed.research.jpa

import jakarta.persistence.*

// Section 4.6 — JPA Role entity stub (Many-to-Many target)
@Entity
@Table(name = "roles")
class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var name: String = "",

    @ManyToMany(mappedBy = "roles")
    var users: MutableList<JpaUser> = mutableListOf()
)
