package org.example.exposed.research.exp.jpa.service

import org.example.exposed.research.exp.jpa.entity.City
import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.repo.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class JpaUserService(private val repo: UserRepository) {

    // Section 4.2 — CRUD via JpaRepository

    fun create(
        name: String,
        email: String,
        age: Int,
        city: City
    ): User {
        val user = User(name = name, email = email, age = age, city = city)
        return repo.save(user)
    }

    fun createUserExample(city: City) {
        val user = User(
            name = "alice",
            email = "alice@example.com",
            age = 30,
            city = city
        )
        repo.save(user)
    }

    fun readUserExample(name: String) =
        repo.findByName(name)

    fun findById(id: Int): User? =
        repo.findById(id).orElse(null)

    fun findAll(): List<User> =
        repo.findAll()

fun update(
    id: Int,
    name: String
): User? {
    val user = repo.findById(id)
        .orElse(null)
        ?: return null
    user.name = name
    return repo.save(user)
}

fun delete(id: Int) {
    repo.deleteById(id)
}

    // Section 4.4 — pagination via Pageable

    fun findPage(page: Int, size: Int): Page<User> =
        repo.findAll(PageRequest.of(page, size, Sort.by("name")))

    // Section 4.8 — dynamic queries via Specification API
    // Note: field access is string-based (root.get<String>("name")) — not checked by the compiler

    fun findUsers(
        name: String?,
        minAge: Int?
    ): List<User> {
        val spec = Specification.where<User> { root, _, cb ->
                name?.let {
                    cb.equal(
                        root.get<String>("name"),
                        it
                    )
                }
            }
            .and { root, _, cb ->
                minAge?.let {
                    cb.greaterThan(
                        root.get("age"),
                        it
                    )
                }
            }
        return repo.findAll(spec)
    }
}