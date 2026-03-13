package org.example.exposed.research.benchmark

import org.example.exposed.research.entity.Cities
import org.example.exposed.research.entity.City
import org.example.exposed.research.entity.Users
import org.example.exposed.research.service.ExposedCrudService
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.springframework.transaction.support.TransactionTemplate

@State(Scope.Benchmark)
open class BenchmarkState {

    lateinit var crudService: ExposedCrudService
    lateinit var transactionTemplate: TransactionTemplate
    var seedUserId: Int = 0
    var seedCityId: Int = 0

    @Setup(Level.Trial)
    fun setupTrial() {
        crudService = BenchmarkConfig.bean()
        transactionTemplate = BenchmarkConfig.bean()
    }

    @Setup(Level.Iteration)
    fun setupIteration() {
        transactionTemplate.execute {
            Users.deleteAll()
            Cities.deleteAll()

            val cityIds = Cities.batchInsert((1..5).toList()) { i ->
                this[Cities.name] = "City$i"
            }.map { it[Cities.id].value }

            seedCityId = cityIds.first()

            val userRows = Users.batchInsert((1..200).toList()) { i ->
                this[Users.name] = "User$i"
                this[Users.email] = "user$i@test.com"
                this[Users.age] = 20 + (i % 50)
                this[Users.city] = org.jetbrains.exposed.v1.core.dao.id.EntityID(cityIds[i % cityIds.size], Cities)
            }
            seedUserId = userRows.first()[Users.id].value
        }
    }

    val seedCity: City
        get() = transactionTemplate.execute { City.findById(seedCityId) }!!
}
