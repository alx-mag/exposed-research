package org.example.exposed.research.exp.service

import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.exp.entity.*
import org.example.exposed.research.exp.mapper.toResponse
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
    fun findAllRich(): List<User> {
        val users = User.all().with(
            User::city,
            User::profile,
            User::roles
        )
        return users.toList()
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
}
