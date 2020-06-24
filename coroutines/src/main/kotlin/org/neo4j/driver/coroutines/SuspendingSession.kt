package org.neo4j.driver.coroutines

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import org.intellij.lang.annotations.Language
import org.neo4j.driver.Bookmark
import org.neo4j.driver.Query
import org.neo4j.driver.TransactionConfig
import org.neo4j.driver.async.AsyncSession
import org.neo4j.driver.async.ResultCursor


class SuspendingSession(val asyncSession: AsyncSession) {

    suspend fun run(@Language("Cypher") query: String) =
        asyncSession.runAsync(query).await().suspending()

    suspend fun run(@Language("Cypher") query: String, params: Map<String, Any?>) =
        asyncSession.runAsync(query, params).await().suspending()

    suspend fun run(@Language("Cypher") query: String, vararg params: Pair<String, Any?>) =
        asyncSession.runAsync(query, mapOf(*params)).await().suspending()


    suspend fun beginTransaction() =
        asyncSession.beginTransactionAsync().await().suspending()

    suspend fun beginTransaction(config: TransactionConfig) =
        asyncSession.beginTransactionAsync(config).await().suspending()


    suspend fun <T> readTransaction(action: suspend SuspendingTransaction.() -> T): T =
        coroutineScope {
            asyncSession.readTransactionAsync { future { action(it.suspending()) } }.await()
        }

    suspend fun <T> readTransaction(config: TransactionConfig, action: suspend SuspendingTransaction.() -> T): T =
        coroutineScope {
            asyncSession.readTransactionAsync({ future { action(it.suspending()) } }, config).await()
        }

    suspend fun <T> writeTransaction(action: suspend SuspendingTransaction.() -> T): T =
        coroutineScope {
            asyncSession.writeTransactionAsync { future { action(it.suspending()) } }.await()
        }

    suspend fun <T> writeTransaction(config: TransactionConfig, action: suspend SuspendingTransaction.() -> T): T =
        coroutineScope {
            asyncSession.writeTransactionAsync({ future { action(it.suspending()) } }, config).await()
        }

    suspend fun run(@Language("Cypher") query: String, config: TransactionConfig): ResultCursor =
        asyncSession.runAsync(query, config).await()

    suspend fun run(@Language("Cypher") query: String, config: TransactionConfig, params: Map<String, Any?>): ResultCursor =
        asyncSession.runAsync(query, params, config).await()

    suspend fun run(@Language("Cypher") query: Query, config: TransactionConfig): ResultCursor =
        asyncSession.runAsync(query, config).await()

    fun lastBookmark(): Bookmark = asyncSession.lastBookmark()

    suspend fun close() { asyncSession.closeAsync().await() }

    suspend fun <T> use(action: suspend SuspendingSession.() -> T): T {
        var exception: Throwable? = null
        try {
            return action(this)
        } catch (e: Throwable) {
            exception = e
            throw e
        } finally {
            when (exception) {
                null -> close()
                else -> try {
                    close()
                } catch (closeException: Throwable) {
                    exception.addSuppressed(closeException)
                }
            }
        }
    }

}

