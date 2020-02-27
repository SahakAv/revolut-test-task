package com.revolut.task.config

import com.revolut.task.config.ApplicationProfile.Companion.DEFAULT_PROFILE
import com.revolut.task.config.ApplicationProfile.Companion.PROFILE_ENV_NAME
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.getConfigAwait
import io.vertx.kotlin.coroutines.dispatcher

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

import java.time.Duration

object ApplicationConfig {
  private val log = LoggerFactory.getLogger(ApplicationConfig::class.java)
  private val profile = System.getenv(PROFILE_ENV_NAME) ?: DEFAULT_PROFILE

  fun initConfigRetriever(vertx: Vertx, completionHandler: Handler<JsonObject>) {
    log.info("Using  profile $profile")
    val options = ConfigRetrieverOptions()
      .setScanPeriod(Duration.ofHours(24L).toMillis())
      .addStore(getPropertiesConfigStoreOptions("conf.properties"))
      .addStore(getPropertiesConfigStoreOptions("conf.$profile.properties"))
      .addStore(getEnvConfigStoreOptions())
    initConfigRetriever(vertx, options, completionHandler)
  }

  fun getProfile(): ApplicationProfile = ApplicationProfile.get(profile)

  private fun initConfigRetriever(vertx: Vertx, options: ConfigRetrieverOptions,
                                  completionHandler: Handler<JsonObject>) {
    GlobalScope.launch(vertx.dispatcher()) {
      val retriever = ConfigRetriever.create(vertx, options)
      val conf = retriever.getConfigAwait()
      printConfig(conf)
      completionHandler.handle(conf)
    }
  }

  private fun printConfig(json: JsonObject) {
    if (profile == "local") {
      log.info("Current configuration: ${json.encodePrettily()}")
    }
  }

  private fun getPropertiesConfigStoreOptions(path: String): ConfigStoreOptions {
    return ConfigStoreOptions()
      .setType("file")
      .setFormat("properties")
      .setConfig(JsonObject().put("path", path))
  }

  private fun getEnvConfigStoreOptions(): ConfigStoreOptions {
    return ConfigStoreOptions()
      .setType("env")
      .setConfig(JsonObject()
        .put("keys", ConfigConstants().getKeys()))
  }
}
