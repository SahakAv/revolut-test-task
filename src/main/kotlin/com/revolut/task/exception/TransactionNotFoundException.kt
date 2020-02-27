package com.revolut.task.exception


data class TransactionNotFoundException(val id: String) : RuntimeServiceException("Transaction with $id not found")