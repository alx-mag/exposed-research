package org.example.exposed.research.dto

data class CreateUserRequest(val name: String, val email: String, val age: Int)

data class UpdateUserRequest(val name: String)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int
)

data class UserRichResponse(
    val id: Int,
    val name: String,
    val email: String,
    val age: Int,
    val roles: Set<String>,
    val city: String?,
    val profileBio: String?,
)

data class UserFilter(
    val name: String? = null,
    val email: String? = null,
    val minAge: Int? = null,
    val maxAge: Int? = null
)