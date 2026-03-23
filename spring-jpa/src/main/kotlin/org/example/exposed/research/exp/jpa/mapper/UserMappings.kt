package org.example.exposed.research.exp.jpa.mapper

import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.exp.jpa.entity.User

fun User.toResponse() = UserResponse(requireNotNull(id), name, email, age)
