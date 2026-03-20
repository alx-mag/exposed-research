package org.example.exposed.research.exp.jpa.benchmark

import org.example.exposed.research.benchmark.AbstractBenchmarkConfig
import org.example.exposed.research.exp.jpa.SpringJpaApplication

object JpaBenchmarkConfig : AbstractBenchmarkConfig() {

    override val applicationClass = SpringJpaApplication::class.java

    override fun additionalProperties() = mapOf(
        "spring.jpa.hibernate.ddl-auto" to "create-drop",
        "spring.jpa.show-sql" to "false",
    )
}
