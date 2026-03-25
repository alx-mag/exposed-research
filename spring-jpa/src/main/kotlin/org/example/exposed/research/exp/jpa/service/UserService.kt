package org.example.exposed.research.exp.jpa.service

import jakarta.persistence.criteria.Predicate
import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.repo.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class UserService(private val repo: UserRepository) {

    fun readUserExample(name: String) =
        repo.findByName(name)

    fun findById(id: Int): User? =
        repo.findById(id).orElse(null)

    fun findAll(): List<User> =
        repo.findAll()

    fun findFiltering(userFilter: UserFilter): List<User> {
        val spec = Specification<User> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            userFilter.name?.let {
                predicates += cb.like(root.get<String>("name"), "%$it%")
            }
            userFilter.email?.let {
                predicates += cb.like(root.get<String>("email"), "%$it%")
            }
            userFilter.minAge?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("age"), it)
            }
            userFilter.maxAge?.let {
                predicates += cb.lessThanOrEqualTo(root.get("age"), it)
            }
            cb.and(*predicates.toTypedArray())
        }
        return repo.findAll(spec)
    }

    fun create(request: CreateUserRequest): User {
        val user = User(
            name = request.name,
            email = request.email,
            age = request.age
        )
        return repo.save(user)
    }

    fun update(
        id: Int,
        request: UpdateUserRequest
    ): User {
        val user = repo.findById(id)
            .orElse(null)
            ?: throw NoSuchElementException("User $id not found")
        user.name = request.name
        return repo.save(user)
    }

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
