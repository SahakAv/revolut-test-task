package com.revolut.task.db

import com.revolut.task.config.ConfigConstants.Companion.DB_URL
import com.revolut.task.config.ConfigConstants.Companion.POOL_SIZE
import com.revolut.task.repository.RepositoryFactory
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.kotlin.coroutines.CoroutineVerticle

class DatabaseVerticle : CoroutineVerticle() {


    private val log = LoggerFactory.getLogger(DatabaseVerticle::class.java)


    override suspend fun start() {
        val poolSize = config.getInteger(POOL_SIZE)

        val config = JsonObject()
            .put("url", config.getString(DB_URL))
            .put("driver_class", "org.hsqldb.jdbcDriver")
            .put("max_pool_size", poolSize);

        val client = JDBCClient.createNonShared(vertx, config)
        RepositoryFactory.setupClient(client)
        log.info("DB connection established successfully")

    }


}