package com.revolut.task.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.getConfigAwait
import kotlinx.coroutines.runBlocking
import java.time.Duration

object Config {

  private lateinit var deploymentOpts: DeploymentOptions
  private val config = ConfigRetrieverOptions()
    .setScanPeriod(Duration.ofHours(24L).toMillis())
    .addStore(ConfigStoreOptions()
      .setType("file")
      .setFormat("properties")
      .setConfig(JsonObject().put("path", "conf.test.local.properties")))


  fun deploymentOptions(vertx: Vertx): DeploymentOptions {
    if (!this::deploymentOpts.isInitialized) {
      deploymentOpts = runBlocking {
        val options = DeploymentOptions().setConfig(
            ConfigRetriever.create(vertx, config).getConfigAwait())
        options
      }
    }
    return this.deploymentOpts
  }


}
