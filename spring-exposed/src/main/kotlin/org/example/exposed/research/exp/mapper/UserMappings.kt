package org.example.exposed.research.exp.mapper

import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.dto.UserRichResponse
import org.example.exposed.research.exp.entity.User

fun User.toResponse() = UserResponse(
    id.value,
    name,
    email,
    age
)

fun User.toRichResponse() = UserRichResponse(
    id.value,
    name,
    email,
    age,
    roles.map { it.name }.toSet(),
    city?.name,
    profile?.bio
)
