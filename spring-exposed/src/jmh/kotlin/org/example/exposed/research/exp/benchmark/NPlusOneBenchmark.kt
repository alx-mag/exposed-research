package org.example.exposed.research.exp.benchmark

import org.example.exposed.research.entity.Cities
import org.example.exposed.research.entity.User
import org.example.exposed.research.entity.Users
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class NPlusOneBenchmark {

    // Section 6.4 — N+1 problem: lazy load vs explicit JOIN

    @Benchmark
    fun nPlusOne(state: BenchmarkState): Int =
        state.transactionTemplate.execute {
            // Each access to user.city triggers a separate SELECT — N+1 queries
            User.all().toList().onEach { it.city }.size
        } ?: 0

    @Benchmark
    fun withJoin(state: BenchmarkState): Int =
        state.transactionTemplate.execute {
            // Single JOIN query — 1 query total
            Users.leftJoin(Cities).selectAll().toList().size
        } ?: 0
}
