package org.example.exposed.research.exp.service

import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.dto.UserRichResponse
import org.example.exposed.research.exp.entity.*
import org.example.exposed.research.exp.mapper.toResponse
import org.example.exposed.research.exp.mapper.toRichResponse
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {

    @Transactional
    fun create(name: String, email: String, age: Int, city: City): User =
        User.new { this.name = name; this.email = email; this.age = age; this.city = city }

    @Transactional
    fun create(request: CreateUserRequest): UserResponse {
        return User.new {
            name = request.name
            email = request.email
            age = request.age
        }.toResponse()
    }

    @Transactional
    fun update(id: Int, request: UpdateUserRequest): User {
        val user = User.findById(id)
            ?: throw NoSuchElementException("User $id not found")
        user.name = request.name
        return user
    }

    @Transactional(readOnly = true)
    fun findById(id: Int): User? =
        User.findById(id)

    @Transactional(readOnly = true)
    fun findAll(): List<User> =
        User.all().toList()

    @Transactional(readOnly = true)
    fun findAllSql(): List<UserResponse> =
        Users.selectAll()
            .orderBy(Users.id to SortOrder.ASC)
            .map { row ->
                UserResponse(
                    id = row[Users.id].value,
                    name = row[Users.name],
                    email = row[Users.email],
                    age = row[Users.age]
                )
            }

    @Transactional(readOnly = true)
    fun findAllRich(): List<UserRichResponse> {
        val users = User.all().with(
            User::city,
            User::profile,
            User::roles
        )
        return users.map { it.toRichResponse() }
    }

    @Transactional(readOnly = true)
    fun findAllRichSql(): List<UserRichResponse> {
        val query = Users
            .leftJoin(Cities, onColumn = { city }, otherColumn = { Cities.id })
            .leftJoin(Profiles, onColumn = { Users.profile }, otherColumn = { Profiles.id })
            .leftJoin(UserRoles, onColumn = { Users.id }, otherColumn = { UserRoles.user })
            .leftJoin(Roles, onColumn = { UserRoles.role }, otherColumn = { Roles.id })
            .selectAll()
            .orderBy(Users.id to SortOrder.ASC, Roles.id to SortOrder.ASC)

        val usersById = linkedMapOf<Int, MutableUserRichResponse>()
        query.forEach { row ->
            val userId = row[Users.id].value
            val user = usersById.getOrPut(userId) {
                MutableUserRichResponse(
                    id = userId,
                    name = row[Users.name],
                    email = row[Users.email],
                    age = row[Users.age],
                    city = row.getOrNull(Cities.name),
                    profileBio = row.getOrNull(Profiles.bio)
                )
            }
            row.getOrNull(Roles.name)?.let(user.roles::add)
        }

        return usersById.values.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findFiltering(userFilter: UserFilter): List<UserResponse> {
        val query = Users.selectAll()
        userFilter.name?.let { name ->
            query.andWhere { Users.name like "%$name%" }
        }
        userFilter.email?.let { email ->
            query.andWhere { Users.email like "%$email%" }
        }
        userFilter.minAge?.let { minAge ->
            query.andWhere { Users.age greaterEq minAge }
        }
        userFilter.maxAge?.let { maxAge ->
            query.andWhere { Users.age lessEq maxAge }
        }
        return User.wrapRows(query).map { it.toResponse() }
    }

    @Transactional
    fun update(
        id: Int,
        name: String
    ): User? {
        val user = User.findById(id)
        user?.name = name
        return user
    }

    @Transactional
    fun delete(id: Int) {
        User.findById(id)?.delete()
    }

    // Section 4.3 — filtering with DAO

    @Transactional(readOnly = true)
    fun findByNameAndAge(name: String, age: Int): List<User> =
        User.find { (Users.name eq name) and (Users.age eq age) }.toList()

    // Section 4.4 — pagination and sorting

    @Transactional(readOnly = true)
    fun findPage(page: Int, size: Int): List<User> =
        User.all()
            .limit(size)
            .offset((page * size).toLong())
            .orderBy(Users.name to SortOrder.ASC)
            .toList()

    // Section 4.5 — custom join query (DSL + DAO wrapping)

    @Transactional(readOnly = true)
    fun findByCityAndMinAge(city: String, minAge: Int): List<User> {
        val rows = Users.innerJoin(Cities)
            .selectAll()
            .where { (Users.age greater minAge) and (Cities.name eq city) }
        return User.wrapRows(rows).toList()
    }

    // Section 4.7 — projection: separate entity class on the same table

    @Transactional(readOnly = true)
    fun findNameEmailByAge(age: Int): List<UserNameEmail> =
        UserNameEmail.find { Users.age eq age }.toList()

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
