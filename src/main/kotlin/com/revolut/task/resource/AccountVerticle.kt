package com.revolut.task.resource

import com.revolut.task.common.AbstractCoroutineVerticle
import com.revolut.task.model.Account
import com.revolut.task.repository.RepositoryFactory
import com.revolut.task.service.AccountService
import com.revolut.task.utils.EventBusAddresses.*
import com.revolut.task.utils.KotlinUtils.addJsonHeader
import com.revolut.task.utils.KotlinUtils.sendResponse
import io.vertx.core.http.HttpMethod.*
import io.vertx.ext.web.RoutingContext
import java.lang.RuntimeException

class AccountVerticle(repositoryFactory: RepositoryFactory) : AbstractCoroutineVerticle() {

    private val accountService = AccountService(repositoryFactory)

    override suspend fun start() {
        eventBusConsumer<Unit>(ACCOUNT) {
            val body = it.body()
            when (body.request().method()) {
                GET  -> getAccount(body)
                POST -> createAccount(body)
                else -> body.response().setStatusCode(404).end()
            }
        }
    }

    private suspend fun createAccount(routingContext: RoutingContext) {
        val account = routingContext.body?.toJsonObject()?.mapTo(Account::class.java) ?:
        throw RuntimeException("Invalid body provided for creating account")
        accountService.createAccount(account)
        routingContext.sendResponse(account)
    }


    private suspend fun getAccount(routingContext: RoutingContext) {
        if (routingContext.request().params().contains("id")) {
            val id = routingContext.request().params()["id"]
            val account = accountService.getAccount(id)
            routingContext.sendResponse(account)
        } else {
            val allAccounts = accountService.getAllAccounts()
            routingContext.sendResponse(allAccounts)
        }
    }
}