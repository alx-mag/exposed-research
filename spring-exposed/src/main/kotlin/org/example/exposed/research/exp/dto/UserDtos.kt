package org.example.exposed.research.exp.dto

import org.example.exposed.research.exp.entity.User

data class CreateUserRequest(val name: String, val email: String, val age: Int)

data class UpdateUserRequest(val name: String)

data class UserResponse(val id: Int, val name: String, val email: String, val age: Int)

fun User.toResponse() = UserResponse(id.value, name, email, age)