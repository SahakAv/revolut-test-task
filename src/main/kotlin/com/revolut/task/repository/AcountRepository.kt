package com.revolut.task.repository

import com.revolut.task.exception.RuntimeServiceException
import com.revolut.task.model.Account
import com.revolut.task.model.Transaction
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.kotlin.coroutines.awaitResult
import io.vertx.kotlin.ext.sql.*
import io.vertx.reactivex.sqlclient.SqlConnection
import java.lang.Exception

class AccountRepository(private val client: JDBCClient) {


    suspend fun findAll(): List<Account> {
        val connection = getConnection()
        val result = connection.queryAwait(SELECT_ALL);
        val accounts = result.results.map { mapToAccount(it) }
        connection.closeAwait()
        return accounts;
    }

    suspend fun find(id: String): Account? {
        val connection = getConnection()
        val result = connection.querySingleAwait(String.format(SELECT_ONE, id));
        val account = result?.let { mapToAccount(it) }
        connection.closeAwait()
        return account;
    }

    suspend fun create(account: Account) {
        val connection = getConnection()

        connection.executeAwait((String.format(INSERT_ONE,
            account.id,
            account.ownerName,
            account.balance,
            account.blockedAmount,
            account.currency)))
        connection.closeAwait()
    }

    suspend fun update(account: Account) {
        val connection = getConnection()
        connection.setAutoCommitAwait(false)
        try {
            connection.updateAwait(String.format(UPDATE_ONE, account.balance, account.blockedAmount, account.id))
            connection.commitAwait()
        } catch (e: Exception) {
            connection.rollbackAwait ()
        } finally {
            connection.close()
        }
    }

    suspend fun validateAndUpdateBalance(sender: Account, transaction: Transaction) {
        val connection = getConnection()
        connection.setAutoCommitAwait(false)
        try {
            val updated = connection.updateAwait(String.format(VALIDATE_BALANCE_AND_UPDATE,
                transaction.amount,
                sender.id,
                transaction.amount))
            if (updated.updated == 1) {
                connection.commitAwait()
            } else {
                throw RuntimeServiceException("Error occurred while trying to block amount in account ${sender.id}")
            }

        } catch (e: Exception) {
            connection.rollbackAwait()
            connection.closeAwait()
            throw e
        } finally {
            connection.close()
        }
    }


    private suspend fun getConnection(): SQLConnection {
        return awaitResult { client.getConnection(it) }
    }

    private fun mapToAccount(jsonArray: JsonArray): Account {
        val id = jsonArray.getString(0)
        val ownerName = jsonArray.getString(1)
        val balance = jsonArray.getDouble(2)
        val blockedAmount = jsonArray.getDouble(3)
        val currency = jsonArray.getString(4)
        return Account(id, ownerName, balance, blockedAmount, currency)
    }


    private companion object {
        private const val TABLE_NAME = "ACCOUNT"
        private const val SELECT_ALL = "SELECT * FROM $TABLE_NAME"
        private const val SELECT_ONE = "SELECT * FROM $TABLE_NAME WHERE ID = '%s'"
        private const val INSERT_ONE = "INSERT INTO  $TABLE_NAME  VALUES ('%s','%s','%s','%s','%s')"
        private const val UPDATE_ONE = "UPDATE $TABLE_NAME SET BALANCE = %s, BLOCKEDAMOUNT = '%s' WHERE ID = '%s'"
        private const val VALIDATE_BALANCE_AND_UPDATE =
            "UPDATE $TABLE_NAME SET BLOCKEDAMOUNT = '%s' WHERE ID = '%s' AND BALANCE >= '%s'"
    }


}