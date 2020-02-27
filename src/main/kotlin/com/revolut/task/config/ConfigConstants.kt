package com.revolut.task.config

import io.vertx.core.json.JsonArray
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberProperties


class ConfigConstants {

    private val log = LoggerFactory.getLogger(ConfigConstants::class.java)

    fun getKeys(): JsonArray = let {
        val keys = JsonArray()
        ConfigConstants::class.companionObject?.memberProperties?.forEach {
            keys.add(it.getter.call(this) as String)
        }
        log.info("Supported environment keys: ${keys.encodePrettily()}")
        keys
    }

    companion object {
        const val POOL_SIZE = "db.pool.size"
        const val DB_URL = "db.url"
        const val SERVER_PORT = "server.port"
        const val SERVER_URL = "server.url"
    }
}
