package org.example.exposed.research.exp

import org.example.exposed.research.SpringExposedApplication
import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<SpringExposedApplication>().with(TestcontainersConfiguration::class).run(*args)
}
