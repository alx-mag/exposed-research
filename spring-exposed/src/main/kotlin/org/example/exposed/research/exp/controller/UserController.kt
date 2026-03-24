package org.example.exposed.research.exp.controller

import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.exp.mapper.toResponse
import org.example.exposed.research.exp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val users: UserService) {

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return users.create(request)
    }

    @PutMapping("{id}")
    fun updateUser(
        @PathVariable id: Int,
        @RequestBody request: UpdateUserRequest
    ): UserResponse {
        return users.update(id, request).toResponse()
    }

    @GetMapping
    fun getUsers(): List<UserResponse> {
        return users.findAll().map { it.toResponse() }
    }

    @GetMapping("/filtering")
    fun getUsersFiltering(userFilter: UserFilter): List<UserResponse> {
        return users.findFiltering(userFilter)
    }
}
