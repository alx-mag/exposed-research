package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

// Section 4.6 — JPA User entity with all relationship types
// Named JpaUser to avoid clash with Exposed's User class in the same compilation
@Entity(name = "JpaUser")
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var name: String = "",
    var email: String = "",
    var age: Int = 0,

    @ManyToOne                              // Many-to-One
    var city: City? = null,

    @OneToOne(cascade = [CascadeType.ALL])  // One-to-One
    var profile: Profile? = null,

    @ManyToMany                             // Many-to-Many
    var roles: MutableList<Role> = mutableListOf()
)