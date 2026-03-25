package org.example.exposed.research.exp.jpa.repo

import org.example.exposed.research.exp.jpa.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Int>
