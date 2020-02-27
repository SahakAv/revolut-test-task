package com.revolut.task.model

import org.joda.time.LocalDateTime
import java.util.*


data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val fromId: String,
    val toId: String,
    val amount: Double,
    val currency: String) : BaseModel