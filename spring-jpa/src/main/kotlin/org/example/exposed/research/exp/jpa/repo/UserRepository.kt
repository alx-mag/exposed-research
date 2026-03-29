package org.example.exposed.research.exp.jpa.repo

import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.entity.UserNameEmail
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

// Sections 4.2, 4.3, 4.5, 4.7, 4.8
interface UserRepository : JpaRepository<User, Int>, JpaSpecificationExecutor<User> {

    fun findByName(name: String): List<User>

    @EntityGraph(attributePaths = ["city", "profile", "roles", "orders"])
    @Query("SELECT u FROM JpaUser u")
    fun findAllWithRelations(): List<User>

    @Query("""SELECT u 
FROM JpaUser u 
WHERE u.age > :minAge AND u.city.name = :city""")
    fun findByCityAndMinAge(
        @Param("city") city: String,
        @Param("minAge") minAge: Int
    ): List<User>
}
