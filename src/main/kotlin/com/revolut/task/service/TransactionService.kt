package com.revolut.task.service

import com.revolut.task.exception.AccountNotFoundException
import com.revolut.task.exception.RuntimeServiceException
import com.revolut.task.exception.TransactionNotFoundException
import com.revolut.task.model.Account
import com.revolut.task.model.Transaction
import com.revolut.task.repository.RepositoryFactory
import kotlinx.coroutines.GlobalScope
import org.slf4j.LoggerFactory
import kotlinx.coroutines.async


class TransactionService(repositoryFactory: RepositoryFactory) {

    private val LOGGER = LoggerFactory.getLogger(AccountService::class.java)
    private val transactionRepository = repositoryFactory.getTransactionRepository()
    private val accountRepository = repositoryFactory.getAccountRepository()


    suspend fun createTransaction(transaction: Transaction) {
        LOGGER.info("Requested to create transaction $transaction")
        validateTransaction(transaction)
        val accounts = validateAccountsExist(transaction)
        val sender = accounts.first
        val receiver = accounts.second
        val amount = transaction.amount
        validateAccountCurrencies(accounts, transaction)
        validateAndUpdateAccountBalance(sender, transaction)
        sender.blockedAmount = sender.blockedAmount + amount
        transactionRepository.create(transaction)
        sender.balance = sender.balance - amount
        sender.blockedAmount = sender.blockedAmount - amount
        accountRepository.update(sender)
        receiver.balance = receiver.balance + amount
        accountRepository.update(receiver)
    }


    suspend fun getTransaction(id: String): Transaction {
        LOGGER.info("Requested to get account with $id")
        return transactionRepository.find(id) ?: throw TransactionNotFoundException(id)
    }

    suspend fun getAllTransactions(): List<Transaction> = let {
        LOGGER.info("Requested to get all transactions")
        transactionRepository.findAll()
    }


    private fun validateAccountCurrencies(accounts: Pair<Account, Account>, transaction: Transaction) {
        val transactionCurrency = transaction.currency
        if (accounts.first.currency != transactionCurrency) {
            throw RuntimeServiceException("Sender account currency don't match to transaction currency")
        }
        if (accounts.second.currency != transactionCurrency) {
            throw RuntimeServiceException("Receiver account currency don't match to transaction currency")
        }
    }

    private suspend fun validateAccountsExist(transaction: Transaction): Pair<Account, Account> {
        val fromAsync = GlobalScope.async { accountRepository.find(transaction.fromId) }
        val toAsync = GlobalScope.async { accountRepository.find(transaction.toId) }
        val sender = fromAsync.await()
        val receiver = toAsync.await()
        if (sender == null) {
            throw AccountNotFoundException(transaction.fromId)
        }
        if (receiver == null) {
            throw AccountNotFoundException(transaction.toId)
        }
        if (sender.balance - sender.blockedAmount < transaction.amount) {
            throw RuntimeServiceException("Sender balance is not enough for performing this operation")
        }
        return sender to receiver
    }

    private suspend fun validateAndUpdateAccountBalance(sender: Account, transaction: Transaction) {
        accountRepository.validateAndUpdateBalance(sender, transaction)
    }


    private suspend fun validateTransaction(transaction: Transaction) {
        transactionRepository.find(transaction.id)
            ?.let { throw  RuntimeServiceException("Transaction with ${transaction.id} already exist") }
        if (transaction.fromId == transaction.toId) {
            throw RuntimeServiceException("Transaction can be be sent from to same account")
        }
        if (transaction.amount <= 0) {
            throw RuntimeServiceException("Invalid transaction amount")
        }
    }


}