package org.example.exposed.research.exp.controller

import org.example.exposed.research.dto.*
import org.example.exposed.research.exp.mapper.toResponse
import org.example.exposed.research.exp.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val users: UserService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return users.create(request)
    }

    @PutMapping("/{id}")
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
        return users.findAllSql()
    }

    @GetMapping("/rich")
    fun getUsersRich(): List<UserRichResponse> {
        return users.findAllRich()
    }

    @GetMapping("/rich/sql")
    fun getUsersRichSql(): List<UserRichResponse> {
        return users.findAllRichSql()
    }

    @GetMapping("/filtering")
    fun getUsersFiltering(userFilter: UserFilter): List<UserResponse> {
        return users.findFiltering(userFilter)
    }
}
