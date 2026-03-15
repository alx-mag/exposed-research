package org.example.exposed.research.exp.benchmark

import org.example.exposed.research.entity.Cities
import org.example.exposed.research.entity.Users
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class DslBenchmark {

    // Section 6.3 — DSL query benchmarks

    @Benchmark
    fun batchInsert(state: BenchmarkState) {
        state.transactionTemplate.execute {
            val items = (1..1000).toList()
            Users.batchInsert(items) { i ->
                this[Users.name] = "Batch$i"
                this[Users.email] = "batch$i@test.com"
                this[Users.age] = 30
                this[Users.city] = EntityID(state.seedCityId, Cities)
            }
        }
    }

    @Benchmark
    fun insertOneByOne(state: BenchmarkState) {
        state.transactionTemplate.execute {
            repeat(1000) { i ->
                Users.insert {
                    it[name] = "One$i"
                    it[email] = "one$i@test.com"
                    it[age] = 30
                    it[city] = EntityID(state.seedCityId, Cities)
                }
            }
        }
    }

    @Benchmark
    fun selectWithJoin(state: BenchmarkState): List<Any> =
        state.transactionTemplate.execute {
            Users.innerJoin(Cities)
                .selectAll()
                .toList()
        } ?: emptyList()

    @Benchmark
    fun bulkUpdate(state: BenchmarkState): Int =
        state.transactionTemplate.execute {
            Users.update({ Users.age less 30 }) {
                it[Users.age] = 99
            }
        } ?: 0
}
