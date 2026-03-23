package org.example.exposed.research.dto

data class CreateUserRequest(val name: String, val email: String, val age: Int)

data class UpdateUserRequest(val name: String)

data class UserResponse(val id: Int, val name: String, val email: String, val age: Int)
