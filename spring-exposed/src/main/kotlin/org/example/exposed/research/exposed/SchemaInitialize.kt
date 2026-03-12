package org.example.exposed.research.exposed

import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

//@Profile("exposed")
//@Component
//@Transactional
//class SchemaInitialize : ApplicationRunner {
//
//    override fun run(args: ApplicationArguments) {
//        SchemaUtils.create(Cities, Profiles, Roles, Users, UserRoles)
//    }
//}