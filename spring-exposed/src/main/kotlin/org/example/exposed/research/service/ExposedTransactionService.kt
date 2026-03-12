package org.example.exposed.research.service

import org.example.exposed.research.dto.UserDto
import org.example.exposed.research.entity.Cities
import org.example.exposed.research.entity.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExposedTransactionService {

    // Section 5.1 — @Transactional with DSL join; no transaction {} block needed
    // WARNING: using transaction {} in Spring Boot bypasses SpringTransactionManager —
    //   @TransactionalEventListener won't fire, propagation won't work, test rollback won't apply.
    //   Only use transaction {} in startup code (ApplicationRunner / @PostConstruct).

    @Transactional(readOnly = true)
    fun findByCity(cityName: String): List<UserDto> =
        Users.innerJoin(Cities)
            .selectAll()
            .where { Cities.name eq cityName }
            .map { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }
}
