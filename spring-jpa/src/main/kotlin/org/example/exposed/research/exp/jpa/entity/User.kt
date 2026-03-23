package org.example.exposed.research.exp.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

// Section 4.6 — JPA User entity with all relationship types
// Named JpaUser to avoid clash with Exposed's User class in the same compilation
@Entity(name = "JpaUser")
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(nullable = false, length = 255)
    var name: String = "",
    @Column(nullable = false, length = 255)
    var email: String = "",
    @Column(nullable = false)
    var age: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    var city: City? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    var profile: Profile? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "userroles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableList<Role> = mutableListOf()
)
