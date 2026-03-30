package org.example.exposed.research.exp.jpa.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.dto.UserRichResponse
import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.repo.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val repo: UserRepository) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun readUserExample(name: String) =
        repo.findByName(name)

    fun findById(id: Int): User? =
        repo.findById(id).orElse(null)

    fun findAll(): List<User> =
        repo.findAll()

    @Transactional(readOnly = true)
    fun findAllNative(): List<UserResponse> =
        entityManager.createNativeQuery(
            """
            select u.id, u.name, u.email, u.age
            from users u
            order by u.id
            """.trimIndent()
        )
            .resultList
            .map { row -> row as Array<*> }
            .map { row ->
                UserResponse(
                    id = (row[0] as Number).toInt(),
                    name = row[1] as String,
                    email = row[2] as String,
                    age = (row[3] as Number).toInt()
                )
            }

    fun findAllRich(): List<User> =
        repo.findAllWithRelations()

    @Transactional(readOnly = true)
    fun findAllRichNative(): List<UserRichResponse> {
        val rows = entityManager.createNativeQuery(
            """
            select u.id,
                   u.name,
                   u.email,
                   u.age,
                   c.name as city_name,
                   p.bio as profile_bio,
                   r.id as role_id,
                   r.name as role_name
            from users u
            left join cities c on c.id = u.city_id
            left join profiles p on p.id = u.profile_id
            left join userroles ur on ur.user_id = u.id
            left join roles r on r.id = ur.role_id
            order by u.id, r.id
            """.trimIndent()
        ).resultList

        val usersById = linkedMapOf<Int, MutableUserRichResponse>()
        rows.forEach { rawRow ->
            val row = rawRow as Array<*>
            val userId = (row[0] as Number).toInt()
            val user = usersById.getOrPut(userId) {
                MutableUserRichResponse(
                    id = userId,
                    name = row[1] as String,
                    email = row[2] as String,
                    age = (row[3] as Number).toInt(),
                    city = row[4] as String?,
                    profileBio = row[5] as String?
                )
            }
            (row[7] as String?)?.let(user.roles::add)
        }

        return usersById.values.map { it.toResponse() }
    }

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

    private data class MutableUserRichResponse(
        val id: Int,
        val name: String,
        val email: String,
        val age: Int,
        val roles: LinkedHashSet<String> = linkedSetOf(),
        val city: String?,
        val profileBio: String?,
    ) {
        fun toResponse() = UserRichResponse(
            id = id,
            name = name,
            email = email,
            age = age,
            roles = roles,
            city = city,
            profileBio = profileBio
        )
    }
}
