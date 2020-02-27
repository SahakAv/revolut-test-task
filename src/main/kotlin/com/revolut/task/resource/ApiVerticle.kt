package com.revolut.task.resource

import com.revolut.task.common.AbstractCoroutineVerticle
import com.revolut.task.utils.EventBusAddresses
import io.vertx.ext.web.RoutingContext
import com.revolut.task.utils.EventBusAddresses.*

class ApiVerticle() : AbstractCoroutineVerticle() {


    override suspend fun start() {
        eventBusConsumer<Unit>(API) {
            when {
                (it.body().request().path()).endsWith(ACCOUNT)
                -> vertx.eventBus().send(EventBusAddresses.ACCOUNT.name, it.body())
                (it.body().request().path()).endsWith(TRANSACTION)
                -> vertx.eventBus().send(EventBusAddresses.TRANSACTION.name, it.body())
                else -> it.body().response().setStatusCode(404).end()
            }
        }
    }


    private companion object {
        private const val ACCOUNT = "account"
        private const val TRANSACTION = "transaction"
    }

}