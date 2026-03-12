package org.example.exposed.research.jpa

import jakarta.persistence.*

// Section 4.6 — JPA User entity with all relationship types
// Named JpaUser to avoid clash with Exposed's User class in the same compilation
@Entity(name = "JpaUser")
@Table(name = "users")
class JpaUser(
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
