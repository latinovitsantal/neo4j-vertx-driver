package org.neo4j.driver.coroutines

import kotlinx.coroutines.future.await
import org.neo4j.driver.Driver
import org.neo4j.driver.Metrics
import org.neo4j.driver.types.TypeSystem

class SuspendingDriver(val driver: Driver) {

    fun session() = driver.asyncSession().suspending()

    suspend fun <T> session(action: suspend SuspendingSession.() -> T) =
        driver.asyncSession().suspending().use(action)

    suspend fun <T> readTransaction(action: suspend SuspendingTransaction.() -> T) =
        session { readTransaction(action) }

    suspend fun <T> writeTransaction(action: suspend SuspendingTransaction.() -> T) =
        session { writeTransaction(action) }

    val isEncrypted get() = driver.isEncrypted
    val isMetricsEnabled get() = driver.isMetricsEnabled
    val defaultTypeSystem: TypeSystem get() = driver.defaultTypeSystem()
    val metrics: Metrics get() = driver.metrics()

    suspend fun close() { driver.closeAsync().await() }
    suspend fun supportsMultiDb(): Boolean = driver.supportsMultiDbAsync().await()
    suspend fun verifyConnectivity() { driver.verifyConnectivityAsync().await() }

}