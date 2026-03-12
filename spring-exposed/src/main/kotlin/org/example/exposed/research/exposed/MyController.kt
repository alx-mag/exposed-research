package org.example.exposed.research.exposed

import org.example.exposed.research.exposed.UserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MyController(private val exposedUserService: ExposedUserService) {

    @GetMapping("/users")
    fun getUsers(): List<UserResponse> {
        return exposedUserService.getUsers()
    }

    @PostMapping("/fill-users")
    fun fillUsers(@RequestBody amount: Int): List<UserResponse> {
        return exposedUserService.fillUsers(amount)
    }
}