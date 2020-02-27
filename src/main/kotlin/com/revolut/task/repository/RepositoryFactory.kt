package com.revolut.task.repository

import io.vertx.ext.jdbc.JDBCClient

object RepositoryFactory {

    private lateinit var accountRepository: AccountRepository
    private lateinit var transactionRepository: TransactionRepository


    fun setupClient(client: JDBCClient) {
        transactionRepository = TransactionRepository(client)
        accountRepository = AccountRepository(client)
    }

    fun getAccountRepository() = accountRepository

    fun getTransactionRepository() = transactionRepository




}