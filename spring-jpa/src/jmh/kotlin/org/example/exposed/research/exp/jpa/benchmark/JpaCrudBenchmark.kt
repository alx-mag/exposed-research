package org.example.exposed.research.exp.jpa.benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class JpaCrudBenchmark {

    @Benchmark
    fun findById(state: BenchmarkState): Any? =
        state.userService.findById(state.seedUserId)

    @Benchmark
    fun create(state: BenchmarkState): Any =
        state.userService.create("BenchUser", "b@test.com", 25, state.seedCity)

    @Benchmark
    fun update(state: BenchmarkState): Any? =
        state.userService.update(state.seedUserId, "Updated")
}
