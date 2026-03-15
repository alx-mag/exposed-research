package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// Section 4.6 — JPA Profile entity stub (One-to-One target)
@Entity
@Table(name = "profiles")
class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var bio: String = ""
)