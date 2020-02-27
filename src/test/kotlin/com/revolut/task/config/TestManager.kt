package com.revolut.task.config

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.task.MainVerticle
import com.revolut.task.utils.GenericMessageCodec
import io.vertx.core.DeploymentOptions
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInfo
import kotlin.reflect.KClass

object TestManager {

    fun registerTimeModules() = DatabindCodec.mapper().registerModules(KotlinModule(), JavaTimeModule())

    fun deployApp(vertx: Vertx) {
        val deploymentOptions = Config.deploymentOptions(vertx)
        registerTimeModules()
        runBlocking {
            vertx.deployVerticleAwait(MainVerticle(), deploymentOptions)
        }
    }


    fun stop(vertx: Vertx) {
        runBlocking {
            awaitResult<Void> { vertx.close(it) }
        }
    }
}

