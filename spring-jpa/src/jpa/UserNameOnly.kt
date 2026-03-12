package org.example.exposed.research.jpa

// Section 4.7 — JPA interface projection (Spring Data resolves this at runtime)
interface UserNameOnly {
    fun getName(): String
    fun getEmail(): String
}
