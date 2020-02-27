package com.revolut.task.service

import com.revolut.task.exception.AccountNotFoundException
import com.revolut.task.exception.RuntimeServiceException
import com.revolut.task.model.Account
import com.revolut.task.repository.RepositoryFactory
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class AccountService(repositoryFactory: RepositoryFactory) {

    private val LOGGER = LoggerFactory.getLogger(AccountService::class.java)
    private val accountRepository = repositoryFactory.getAccountRepository()


    suspend fun createAccount(account: Account): Account {
        LOGGER.info("Requested to create account $account")
        if(accountRepository.find(account.id) != null){
            throw RuntimeServiceException("Account with ${account.id} already exist")
        }
        accountRepository.create(account)
        return account
    }

    suspend fun getAccount(id: String): Account {
        LOGGER.info("Requested to get account with id $id")
        return accountRepository.find(id) ?: throw AccountNotFoundException(id)
    }

    suspend fun getAllAccounts(): List<Account> = let {
        LOGGER.info("Requested to get all accounts")
        accountRepository.findAll()
    }

}