package com.revolut.task

import com.revolut.task.config.ApplicationServer
import com.revolut.task.dataloader.DataLoaderVerticle
import com.revolut.task.db.DatabaseMigrationVerticle
import com.revolut.task.db.DatabaseVerticle
import com.revolut.task.repository.RepositoryFactory
import com.revolut.task.resource.AccountVerticle
import com.revolut.task.resource.ApiVerticle
import com.revolut.task.resource.TransactionVerticle
import com.revolut.task.utils.GenericMessageCodec
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxException
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.impl.HttpServerResponseImpl
import io.vertx.ext.web.impl.RoutingContextImpl
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle


class MainVerticle : CoroutineVerticle(){


    override suspend fun start() {
        registerCodecs(vertx)
        vertx.deployVerticleAwait(DatabaseVerticle(), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(DatabaseMigrationVerticle(), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(ApplicationServer(), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(ApiVerticle(), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(AccountVerticle(RepositoryFactory), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(TransactionVerticle(RepositoryFactory), DeploymentOptions().setConfig(config))
        vertx.deployVerticleAwait(DataLoaderVerticle(RepositoryFactory))
    }



    companion object{

        fun registerCodecs(vertx: Vertx) {
            vertx.eventBus().registerDefaultCodecForClass(VertxException::class.java)
            vertx.eventBus().registerDefaultCodecForClass(Pair::class.java)
            vertx.eventBus().registerDefaultCodecForClass(Triple::class.java)
            vertx.eventBus().registerDefaultCodecForClass(ArrayList::class.java)
            vertx.eventBus().registerDefaultCodecForClass(Unit::class.java)
            vertx.eventBus().registerDefaultCodecForClass(RoutingContextImpl::class.java)
            vertx.eventBus().registerDefaultCodecForClass(HttpServerResponseImpl::class.java)

        }

        private fun <T> EventBus.registerDefaultCodecForClass(clazz: Class<T>): EventBus = registerDefaultCodec(clazz,
            GenericMessageCodec(clazz))
    }
}