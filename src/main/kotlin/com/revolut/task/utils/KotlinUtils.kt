package com.revolut.task.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.revolut.task.model.BaseModel
import io.vertx.ext.web.RoutingContext

object KotlinUtils {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    fun RoutingContext.addJsonHeader() {
        this.response().putHeader("Content-Type", "application/json")
    }

    fun <T : BaseModel> RoutingContext.sendResponse(body: T?) {
        this.addJsonHeader()
        this.response().setStatusCode(200).end(body?.let { mapper.writeValueAsString(it) })

    }

    fun <T : BaseModel> RoutingContext.sendResponse(body: List<T>?) {
        this.addJsonHeader()
        this.response().setStatusCode(200).end(body?.let { mapper.writeValueAsString(it)})

    }
}