package org.example.exposed.research.exp.controller

import org.example.exposed.research.exp.dto.CreateUserRequest
import org.example.exposed.research.exp.dto.UpdateUserRequest
import org.example.exposed.research.exp.dto.UserResponse
import org.example.exposed.research.exp.dto.toResponse
import org.example.exposed.research.exp.service.ExposedCrudService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class MyController(private val service: ExposedCrudService) {

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return service.create(request)
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Int,
        @RequestBody request: UpdateUserRequest
    ): UserResponse {
        return service.update(id, request).toResponse()
    }

    @GetMapping("/users")
    fun getUsers(): List<UserResponse> {
        return service.findAll().map { it.toResponse() }
    }
}