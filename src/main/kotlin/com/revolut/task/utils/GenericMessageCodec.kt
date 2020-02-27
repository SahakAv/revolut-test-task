package com.revolut.task.utils

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec

class GenericMessageCodec<T>(private val clazz: Class<T>) : MessageCodec<T, T> {
    override fun decodeFromWire(pos: Int, buffer: Buffer?): T {
        //Not need to implement as it will be used only in clustered instance
        TODO("not implemented")
    }

    override fun systemCodecID(): Byte = -1

    override fun encodeToWire(buffer: Buffer?, s: T) {
        //Not need to implement as it will be used only in clustered instance
        TODO("not implemented")
    }

    // non-cluster eventBus will call this method
    override fun transform(s: T): T = s

    override fun name(): String = clazz.name
}
