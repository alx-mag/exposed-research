package org.example.exposed.research.exp.jpa.entity

// Section 4.7 — JPA interface projection (Spring Data resolves this at runtime)
interface UserNameEmail {
    fun getName(): String
    fun getEmail(): String
}