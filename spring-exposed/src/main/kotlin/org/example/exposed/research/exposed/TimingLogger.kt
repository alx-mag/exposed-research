package org.example.exposed.research.exposed

import org.jetbrains.exposed.v1.core.SqlLogger
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.expandArgs

// Section 5.5 — custom SqlLogger that prints query duration alongside SQL with expanded parameters
object TimingLogger : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        println("[${transaction.duration}ms] ${context.expandArgs(transaction)}")
    }
}
