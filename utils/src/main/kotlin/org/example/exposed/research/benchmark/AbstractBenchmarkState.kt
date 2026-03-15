package org.example.exposed.research.benchmark

import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
abstract class AbstractBenchmarkState {
    var seedUserId: Int = 0
    var seedCityId: Int = 0

    @Setup(Level.Trial)
    open fun setupTrial() {}

    @Setup(Level.Iteration)
    open fun setupIteration() {}
}
