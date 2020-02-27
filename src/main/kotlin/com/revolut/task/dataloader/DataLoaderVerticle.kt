package com.revolut.task.dataloader

import com.revolut.task.model.Account
import com.revolut.task.model.Transaction
import com.revolut.task.repository.RepositoryFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataLoaderVerticle(private val repositoryFactory: RepositoryFactory) : CoroutineVerticle() {

    private val accountRepository = repositoryFactory.getAccountRepository()
    private val transactionRepository = repositoryFactory.getTransactionRepository()


    override suspend fun start() {
            val firstAccount = Account(ownerName = "John", balance = 5000.0, blockedAmount = 0.0, currency = "EUR")
            val secondAccount = Account(ownerName = "Tom", balance = 250.0, blockedAmount = 0.0, currency = "EUR")
            accountRepository.create(firstAccount)
            accountRepository.create(secondAccount)
            transactionRepository.create(Transaction(fromId = firstAccount.id,
                toId = secondAccount.id,
                amount = 50.0,
                currency = "EUR"))
            transactionRepository.create(Transaction(fromId = secondAccount.id,
                toId = firstAccount.id,
                amount = 15.0,
                currency = "EUR"))

    }
}