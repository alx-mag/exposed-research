package org.example.exposed.research.exposed

import org.example.exposed.research.exposed.UserResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExposedUserService {

    @Transactional
    fun getUsers(): List<UserResponse> {
        return User.all().map { it.toResponse() }
    }

    @Transactional
    fun fillUsers(amount: Int): List<UserResponse> = (0 until amount).map { i ->
        User.new {
            name = "User Name $i"
        }
    }.map { it.toResponse() }
}