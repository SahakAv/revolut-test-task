package com.revolut.task.common

import com.revolut.task.exception.RuntimeServiceException
import io.vertx.core.eventbus.Message
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import com.revolut.task.utils.EventBusAddresses
import com.revolut.task.utils.KotlinUtils.addJsonHeader
import io.vertx.ext.web.RoutingContext

abstract class AbstractCoroutineVerticle: CoroutineVerticle() {

    protected fun <OUT> eventBusConsumer(address: EventBusAddresses, handler: suspend (Message<RoutingContext>) -> OUT) {
        vertx.eventBus().consumer<RoutingContext>(address.name) {
            launch(vertx.dispatcher()) {
                try {
                    it.reply(handler(it))
                } catch (e: Exception) {
                    val message = if(e is RuntimeServiceException) e.msg else ""
                    it.body().addJsonHeader()
                    it.body().response().setStatusCode(400).end(message)
                }
            }
        }
    }




}
