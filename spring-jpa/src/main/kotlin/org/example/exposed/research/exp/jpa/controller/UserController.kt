package org.example.exposed.research.exp.jpa.controller

import org.example.exposed.research.dto.CreateUserRequest
import org.example.exposed.research.dto.UpdateUserRequest
import org.example.exposed.research.dto.UserFilter
import org.example.exposed.research.dto.UserResponse
import org.example.exposed.research.dto.UserRichResponse
import org.example.exposed.research.exp.jpa.mapper.toResponse
import org.example.exposed.research.exp.jpa.mapper.toRichResponse
import org.example.exposed.research.exp.jpa.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val users: UserService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return users.create(request).toResponse()
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

    @GetMapping("/sql")
    fun getUsersSql(): List<UserResponse> {
        return users.findAllNative()
    }

    @GetMapping("/rich")
    fun getUsersRich(): List<UserRichResponse> {
        return users.findAllRich().map { it.toRichResponse() }
    }

    @GetMapping("/rich/sql")
    fun getUsersRichSql(): List<UserRichResponse> {
        return users.findAllRichNative()
    }

    @GetMapping("/filtering")
    fun getUsersFiltering(userFilter: UserFilter): List<UserResponse> {
        return users.findFiltering(userFilter).map { it.toResponse() }
    }
}
