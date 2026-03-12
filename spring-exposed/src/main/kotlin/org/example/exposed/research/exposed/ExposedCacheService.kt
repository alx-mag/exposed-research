package org.example.exposed.research.exposed

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExposedCacheService {

    // Section 5.4 — L2-style caching with Spring Cache
    // Exposed DAO caches entities within a transaction; for cross-transaction caching use Spring Cache.

    @Transactional(readOnly = true)
    @Cacheable(value = ["users"], key = "#id")
    fun findById(id: Int): UserDto? =
        Users.selectAll()
            .where { Users.id eq id }
            .firstOrNull()
            ?.let { UserDto(it[Users.id].value, it[Users.name], it[Users.age]) }

    @Transactional
    @CacheEvict(value = ["users"], key = "#id")
    fun update(id: Int, name: String) {
        Users.update({ Users.id eq id }) { it[Users.name] = name }
    }
}
