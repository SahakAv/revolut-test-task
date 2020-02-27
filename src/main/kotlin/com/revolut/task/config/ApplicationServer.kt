package com.revolut.task.config

import com.revolut.task.config.ConfigConstants.Companion.SERVER_PORT
import com.revolut.task.config.ConfigConstants.Companion.SERVER_URL
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import org.slf4j.LoggerFactory
import com.revolut.task.utils.EventBusAddresses

class ApplicationServer : CoroutineVerticle() {


   private val log = LoggerFactory.getLogger(ApplicationServer::class.java)
    private val server_url by lazy { config.getString(SERVER_URL, "/api/*") }
    private val port by lazy { config.getInteger(SERVER_PORT, 8080) }

    override suspend fun start() {
        initServer()
        super.start()
    }

    private suspend fun initServer() {
        httpServer().requestHandler(router()).listenAwait(port)
        log.info("Server is up and running on port $port")
    }

    private fun httpServer(): HttpServer {
        val httpServerOptions = HttpServerOptions().setWebsocketSubProtocols("graphql-ws")
        return vertx.createHttpServer(httpServerOptions)
    }

    private suspend fun router(): Router {
        val router = Router.router(vertx)
        configureServerRoute(router)
        return router
    }


    private fun configureServerRoute(router: Router) {
        router.route(server_url)
            .handler(BodyHandler.create())
            .handler { rc -> vertx.eventBus().send(EventBusAddresses.API.name, rc)
            }
    }
}