package org.example.exposed.research.exp.benchmark

import org.example.exposed.research.benchmark.AbstractBenchmarkConfig
import org.example.exposed.research.exp.SpringExposedApplication

object BenchmarkConfig : AbstractBenchmarkConfig() {
    override val applicationClass = SpringExposedApplication::class.java
    override fun additionalProperties() = mapOf(
        "spring.jpa.hibernate.ddl-auto" to "none",
        "spring.exposed.generate-ddl" to "true",
        "spring.exposed.show-sql" to "false",
    )
}
