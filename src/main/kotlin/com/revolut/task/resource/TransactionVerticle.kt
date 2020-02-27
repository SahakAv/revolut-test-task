package com.revolut.task.resource

import com.revolut.task.common.AbstractCoroutineVerticle
import com.revolut.task.model.Account
import com.revolut.task.model.Transaction
import com.revolut.task.repository.RepositoryFactory
import com.revolut.task.service.TransactionService
import com.revolut.task.utils.EventBusAddresses
import com.revolut.task.utils.KotlinUtils.sendResponse
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import java.lang.RuntimeException

class TransactionVerticle(repositoryFactory: RepositoryFactory) : AbstractCoroutineVerticle() {

    private val transactionService = TransactionService(repositoryFactory)

    override suspend fun start() {
        eventBusConsumer<Unit>(EventBusAddresses.TRANSACTION) {
            when (it.body().request().method()) {
                HttpMethod.GET  -> getTransaction(it.body())
                HttpMethod.POST -> createTransaction(it.body())
                else            -> it.body().response().setStatusCode(404).end()
            }
            it.body().request().exceptionHandler { t ->
                it.body().response().setStatusCode(400).end(t.message)
            }
        }
    }

    private suspend fun createTransaction(routingContext: RoutingContext) {
        val transaction = routingContext.body?.toJsonObject()?.mapTo(Transaction::class.java) ?: throw RuntimeException(
            "Invalid body provided for creating transaction")
        transactionService.createTransaction(transaction)
        routingContext.sendResponse(transaction)

    }

    private suspend fun getTransaction(routingContext: RoutingContext) {
        if (routingContext.request().params().contains("id")) {
            val id = routingContext.request().params()["id"]
            val transaction = transactionService.getTransaction(id)
            routingContext.sendResponse(transaction)
        } else {
            val allTransactions = transactionService.getAllTransactions()
            routingContext.sendResponse(allTransactions)
        }
    }


}