package org.example.exposed.research.service

import org.example.exposed.research.dto.UserDto
import org.example.exposed.research.entity.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExposedDynamicQueryService {

    // Section 4.8 — dynamic queries: same DSL, no Specification/Criteria API needed

    @Transactional(readOnly = true)
    fun findUsers(name: String?, minAge: Int?): List<UserDto> {
        val query = Users.selectAll()

        name?.let { query.andWhere { Users.name eq it } }
        minAge?.let { query.andWhere { Users.age greaterEq it } }

        return query.map { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }
    }
}
