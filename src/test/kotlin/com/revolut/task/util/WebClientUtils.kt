package com.revolut.task.util

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxTestContext

fun preConfigureRequest(httpMethod: HttpMethod, vertx: Vertx, path: String,
                        responsePredicate: ResponsePredicate = ResponsePredicate.SC_OK ): HttpRequest<Buffer> {
    return WebClient.create(vertx).request(httpMethod, 8080, "localhost", path).ssl(false)
        .expect(responsePredicate).expect(ResponsePredicate.JSON)
}

fun sendRequestAndAssert(
    vertx: Vertx,
    context: VertxTestContext,
    request: JsonObject,
    httpMethod: HttpMethod,
    path: String,
    responsePredicate: ResponsePredicate = ResponsePredicate.SC_OK,
    handler: Handler<HttpResponse<Buffer>>) {
    preConfigureRequest(httpMethod, vertx, path, responsePredicate).sendJsonObject(request, context.succeeding {

            handler.handle(it)

    })
}
