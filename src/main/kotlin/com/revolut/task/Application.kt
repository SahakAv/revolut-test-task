package com.revolut.task

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.task.config.ApplicationConfig
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import java.time.Duration
import kotlin.system.exitProcess

@Suppress("deprecation")
fun configureVertxJacksonMapper() {
    Json.mapper.registerModules(KotlinModule())

    Json.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}

class App {

    fun start() {
        val options = VertxOptions()
            .setBlockedThreadCheckInterval(Duration.ofSeconds(10).toMillis())
        val vertx = Vertx.vertx(options)
        ApplicationConfig.initConfigRetriever(vertx, Handler {
            deployApp(vertx, it)
        })
    }

    private fun deployApp(vertx: Vertx, config: JsonObject) {
        vertx.deployVerticle(
            MainVerticle(), DeploymentOptions().setConfig(config)
        ) {
            if (it.succeeded()) println(APPLICATION_STARTED)
            else {
                exit(it.cause())
            }
        }
    }

    companion object {
        init {
            configureVertxJacksonMapper()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            App().start()

        }

        fun exit(throwable: Throwable) {
            throwable.printStackTrace()
            println(APPLICATION_FAILED.format(throwable.cause))
            exitProcess(-1)
        }

        private const val APPLICATION_STARTED = """
******************************
Application started
******************************
"""

        private const val APPLICATION_FAILED = """
******************************
Could not start application:
%s
******************************
"""
    }
}