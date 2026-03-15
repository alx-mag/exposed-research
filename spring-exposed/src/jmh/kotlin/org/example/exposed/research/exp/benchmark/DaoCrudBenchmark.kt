package org.example.exposed.research.exp.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class DaoCrudBenchmark {

    // Section 6.2 — DAO CRUD benchmarks

    @Benchmark
    fun findById(state: BenchmarkState): Any? =
        state.crudService.findById(state.seedUserId)

    @Benchmark
    fun create(state: BenchmarkState): Any =
        state.crudService.create("BenchUser", "b@test.com", 25, state.seedCity)

    @Benchmark
    fun update(state: BenchmarkState): Any? =
        state.crudService.update(state.seedUserId, "Updated")
}
