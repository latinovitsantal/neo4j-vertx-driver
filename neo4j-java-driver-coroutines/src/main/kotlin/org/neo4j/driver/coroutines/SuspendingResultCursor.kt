package org.neo4j.driver.coroutines

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.neo4j.driver.Record
import org.neo4j.driver.async.ResultCursor
import org.neo4j.driver.summary.ResultSummary
import kotlin.coroutines.suspendCoroutine

class SuspendingResultCursor(private val cursor: ResultCursor) {
    fun keys(): List<String> = cursor.keys()
    suspend fun next(): Record? = cursor.nextAsync().await()
    suspend fun peek(): Record? = cursor.peekAsync().await()
    suspend fun single(): Record = cursor.singleAsync().await()
    suspend fun consume(): ResultSummary = cursor.consumeAsync().await()
    suspend fun list(): List<Record> = cursor.listAsync().await()
    suspend fun forEach(action: suspend (Record) -> Unit) = suspendCoroutine<Unit> { continuation ->
        cursor.forEachAsync { r -> runBlocking(continuation.context) { action(r) } }
    }
}