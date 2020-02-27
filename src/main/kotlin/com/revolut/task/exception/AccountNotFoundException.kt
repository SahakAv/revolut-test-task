package com.revolut.task.exception


data class AccountNotFoundException(val id: String) : RuntimeServiceException("Account with $id not found")