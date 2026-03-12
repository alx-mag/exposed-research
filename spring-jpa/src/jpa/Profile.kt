package org.example.exposed.research.jpa

import jakarta.persistence.*

// Section 4.6 — JPA Profile entity stub (One-to-One target)
@Entity
@Table(name = "profiles")
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var bio: String = ""
)
