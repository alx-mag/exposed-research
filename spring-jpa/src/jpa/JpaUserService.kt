package org.example.exposed.research.jpa

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class JpaUserService(private val userRepository: UserRepository) {

    // Section 4.2 — CRUD via JpaRepository

    fun create(name: String, email: String, age: Int, city: City): JpaUser =
        userRepository.save(JpaUser(name = name, email = email, age = age, city = city))

    fun findById(id: Int): JpaUser? =
        userRepository.findById(id).orElse(null)

    fun findAll(): List<JpaUser> =
        userRepository.findAll()

    fun update(id: Int, name: String): JpaUser? {
        val user = userRepository.findById(id).orElse(null) ?: return null
        user.name = name
        return userRepository.save(user)
    }

    fun delete(id: Int) =
        userRepository.deleteById(id)

    // Section 4.4 — pagination via Pageable

    fun findPage(page: Int, size: Int): Page<JpaUser> =
        userRepository.findAll(PageRequest.of(page, size, Sort.by("name")))

    // Section 4.8 — dynamic queries via Specification API
    // Note: field access is string-based (root.get<String>("name")) — not checked by the compiler

    fun findUsers(name: String?, minAge: Int?): List<JpaUser> {
        val spec = Specification.where<JpaUser> { root, _, cb ->
                name?.let { cb.equal(root.get<String>("name"), it) }
            }
            .and { root, _, cb ->
                minAge?.let { cb.greaterThan(root.get("age"), it) }
            }
        return userRepository.findAll(spec)
    }
}
