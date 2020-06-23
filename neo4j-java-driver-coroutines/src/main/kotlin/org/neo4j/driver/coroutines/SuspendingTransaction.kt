package org.neo4j.driver.coroutines

import kotlinx.coroutines.future.await
import org.intellij.lang.annotations.Language
import org.neo4j.driver.async.AsyncTransaction

class SuspendingTransaction(val asyncTransaction: AsyncTransaction) {

    suspend fun run(@Language("Cypher") query: String) =
        asyncTransaction.runAsync(query).await().suspending()

    suspend fun run(@Language("Cypher") query: String, params: Map<String, Any?>) =
        asyncTransaction.runAsync(query, params).await().suspending()

    suspend fun run(@Language("Cypher") query: String, vararg params: Pair<String, Any?>) =
        asyncTransaction.runAsync(query, mapOf(*params)).await().suspending()


    suspend fun rollback() { asyncTransaction.rollbackAsync().await() }

    suspend fun commit() { asyncTransaction.commitAsync().await() }

}