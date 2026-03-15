package org.example.exposed.research.exp.jpa.benchmark

import org.example.exposed.research.benchmark.AbstractBenchmarkState
import org.example.exposed.research.exp.jpa.entity.City
import org.example.exposed.research.exp.jpa.entity.User
import org.example.exposed.research.exp.jpa.repo.CityRepository
import org.example.exposed.research.exp.jpa.repo.UserRepository
import org.example.exposed.research.exp.jpa.service.JpaUserService
import org.springframework.transaction.support.TransactionTemplate

open class BenchmarkState : AbstractBenchmarkState() {

    lateinit var userService: JpaUserService
    lateinit var userRepository: UserRepository
    lateinit var cityRepository: CityRepository
    lateinit var transactionTemplate: TransactionTemplate
    lateinit var seedCity: City

    override fun setupTrial() {
        userService = BenchmarkConfig.bean()
        userRepository = BenchmarkConfig.bean()
        cityRepository = BenchmarkConfig.bean()
        transactionTemplate = BenchmarkConfig.bean()
    }

    override fun setupIteration() {
        transactionTemplate.execute {
            userRepository.deleteAll()
            cityRepository.deleteAll()

            val cities = cityRepository.saveAll((1..5).map { City(name = "City$it") })
            seedCityId = cities.first().id!!
            seedCity = cities.first()

            val users = userRepository.saveAll((1..200).mapIndexed { i, _ ->
                User(
                    name = "User${i + 1}",
                    email = "user${i + 1}@test.com",
                    age = 20 + (i % 50),
                    city = cities[i % cities.size]
                )
            })
            seedUserId = users.first().id!!
        }
    }
}
