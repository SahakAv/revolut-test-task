package com.revolut.task.model

import java.util.*

data class Account(
    val id: String = UUID.randomUUID().toString(),
    val ownerName: String,
    var balance: Double,
    var blockedAmount: Double,
    val currency: String
) : BaseModel


