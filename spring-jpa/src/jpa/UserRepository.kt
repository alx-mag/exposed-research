package org.example.exposed.research.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

// Sections 4.2, 4.3, 4.5, 4.7, 4.8
interface UserRepository : JpaRepository<JpaUser, Int>, JpaSpecificationExecutor<JpaUser> {

    // Section 4.3 — derived query method
    fun findByNameAndAge(name: String, age: Int): List<JpaUser>

    // Section 4.7 — interface projection
    fun findByAge(age: Int): List<UserNameOnly>

    // Section 4.5 — custom JPQL query (string-based; errors surface at runtime)
    @Query("SELECT u FROM JpaUser u WHERE u.age > :minAge AND u.city.name = :city")
    fun findByCityAndMinAge(
        @Param("city") city: String,
        @Param("minAge") minAge: Int
    ): List<JpaUser>
}
