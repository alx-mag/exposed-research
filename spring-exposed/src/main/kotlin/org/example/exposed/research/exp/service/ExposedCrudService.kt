package org.example.exposed.research.exp.service

import org.example.exposed.research.exp.dto.CreateUserRequest
import org.example.exposed.research.exp.dto.UpdateUserRequest
import org.example.exposed.research.exp.dto.UserResponse
import org.example.exposed.research.exp.dto.toResponse
import org.example.exposed.research.exp.entity.*
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExposedCrudService {

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
