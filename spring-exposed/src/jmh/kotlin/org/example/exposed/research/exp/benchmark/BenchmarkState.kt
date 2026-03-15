package org.example.exposed.research.exp.benchmark

import org.example.exposed.research.benchmark.AbstractBenchmarkState
import org.example.exposed.research.exp.entity.Cities
import org.example.exposed.research.exp.entity.City
import org.example.exposed.research.exp.entity.Users
import org.example.exposed.research.exp.service.ExposedCrudService
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.springframework.transaction.support.TransactionTemplate

open class BenchmarkState : AbstractBenchmarkState() {

    lateinit var crudService: ExposedCrudService
    lateinit var transactionTemplate: TransactionTemplate

    override fun setupTrial() {
        crudService = BenchmarkConfig.bean()
        transactionTemplate = BenchmarkConfig.bean()
    }

    override fun setupIteration() {
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
                this[Users.city] = EntityID(cityIds[i % cityIds.size], Cities)
            }
            seedUserId = userRows.first()[Users.id].value
        }
    }

    val seedCity: City
        get() = transactionTemplate.execute { City.findById(seedCityId) }!!
}
