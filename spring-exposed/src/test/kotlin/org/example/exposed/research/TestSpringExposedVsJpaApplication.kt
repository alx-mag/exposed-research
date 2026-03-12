package org.example.exposed.research

import org.example.exposed.research.exposed.SpringExposedApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<SpringExposedApplication>().with(TestcontainersConfiguration::class).run(*args)
}
