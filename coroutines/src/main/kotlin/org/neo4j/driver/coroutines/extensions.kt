package org.neo4j.driver.coroutines

import org.neo4j.driver.Driver
import org.neo4j.driver.async.AsyncSession
import org.neo4j.driver.async.AsyncTransaction
import org.neo4j.driver.async.ResultCursor

fun Driver.suspending() = SuspendingDriver(this)
fun AsyncSession.suspending() = SuspendingSession(this)
fun ResultCursor.suspending() = SuspendingResultCursor(this)
fun AsyncTransaction.suspending() = SuspendingTransaction(this)