package org.example.exposed.research.exp.controller

import org.example.exposed.research.exp.entity.Cities
import org.example.exposed.research.exp.entity.Profiles
import org.example.exposed.research.exp.entity.Roles
import org.example.exposed.research.exp.entity.Users
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/admin")
class AdminController {

    @GetMapping("/init-script")
    fun getString(): String {
        val statements = transaction {
            SchemaUtils.createStatements(
                Cities,
                Profiles,
                Roles,
                Users
            )
        }

        return statements.joinToString("\n")
    }
}
