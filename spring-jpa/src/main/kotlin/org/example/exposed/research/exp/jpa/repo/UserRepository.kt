package org.example.exposed.research.exp.jpa.repo

import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.entity.UserNameEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

// Sections 4.2, 4.3, 4.5, 4.7, 4.8
interface UserRepository : JpaRepository<User, Int>, JpaSpecificationExecutor<User> {

    // Section 4.3 — derived query method
    fun findByNameAndAge(name: String, age: Int): List<User>

    // Section 4.7 — interface projection
    fun findByAge(age: Int): List<UserNameEmail>

    // Section 4.5 — custom JPQL query (string-based; errors surface at runtime)
    @Query("SELECT u FROM JpaUser u WHERE u.age > :minAge AND u.city.name = :city")
    fun findByCityAndMinAge(
        @Param("city") city: String,
        @Param("minAge") minAge: Int
    ): List<User>
}
