package com.revolut.task.repository

import com.revolut.task.model.Transaction
import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.ext.sql.closeAwait
import io.vertx.kotlin.ext.sql.executeAwait
import io.vertx.kotlin.ext.sql.queryAwait
import io.vertx.kotlin.ext.sql.querySingleAwait

class TransactionRepository(private val client: JDBCClient) {


    suspend fun findAll(): List<Transaction> {
        val connection = getConnection()
        val result = connection.queryAwait(SELECT_ALL);
        val mappedResult =  result.results.map { mapToTransaction(it) }
        connection.closeAwait()
        return mappedResult
    }

    suspend fun find(id: String): Transaction? {
        val connection = getConnection()
        val result = connection.querySingleAwait(String.format(SELECT_ONE, id));
        val mappedResult = result?.let { mapToTransaction(it) }
        connection.closeAwait()
        return mappedResult
    }

    suspend fun create(transaction: Transaction) {
        val connection = getConnection()
        connection.executeAwait((String.format(INSERT_ONE,
            transaction.id,
            transaction.fromId,
            transaction.toId,
            transaction.amount,
            transaction.currency)))
        connection.closeAwait()
    }


    private suspend fun getConnection(): SQLConnection {
        return awaitResult { client.getConnection(it) }
    }

    private fun mapToTransaction(jsonArray: JsonArray): Transaction {
        val id = jsonArray.getString(0)
        val fromId = jsonArray.getString(1)
        val toId = jsonArray.getString(2)
        val amount = jsonArray.getDouble(3)
        val currency = jsonArray.getString(4)
        return Transaction(id, fromId, toId, amount, currency)
    }

    private companion object {
        private const val TABLE_NAME = "TRANSACTION"
        private const val SELECT_ALL = "SELECT * FROM $TABLE_NAME"
        private const val SELECT_ONE = "SELECT * FROM $TABLE_NAME WHERE ID = '%s'"
        private const val INSERT_ONE = "INSERT INTO  $TABLE_NAME  VALUES ('%s','%s','%s','%s','%s')"
    }

}