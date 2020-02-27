package com.revolut.task.model

import io.vertx.core.json.JsonObject


interface BaseModel {


    fun toJson(): JsonObject {
        return JsonObject.mapFrom(this)
    }
}