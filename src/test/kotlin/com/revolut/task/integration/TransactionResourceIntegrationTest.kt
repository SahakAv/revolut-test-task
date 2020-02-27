package com.revolut.task.integration

import com.revolut.task.config.TestManager
import com.revolut.task.model.Account
import com.revolut.task.model.Transaction
import com.revolut.task.util.sendRequestAndAssert
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.predicate.ResponsePredicate.SC_BAD_REQUEST
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [VertxExtension::class])
class TransactionResourceIntegrationTest {

    companion object {
        private const val ACCOUNT_API_PATH = "/api/account"
        private const val TRANSACTION_API_PATH = "/api/transaction"

        private val accounts = mutableListOf<Account>()
        @JvmStatic
        @BeforeAll
        fun setup(vertx: Vertx, context: VertxTestContext) {
            TestManager.deployApp(vertx)
            sendRequestAndAssert(vertx, context, JsonObject(), HttpMethod.GET, ACCOUNT_API_PATH, handler = Handler {
                accounts.addAll(it.body().toJsonArray().map { (it as JsonObject).mapTo(Account::class.java) })
                context.completeNow()
            })
        }

        @JvmStatic
        @AfterAll
        fun cleanup(vertx: Vertx) {
            TestManager.stop(vertx)
        }
    }

    //Start negative cases block

    @Test
    fun `Should fail create transaction from to same account`(info: TestInfo, vertx: Vertx, context: VertxTestContext) {
        val account = accounts.first()
        val transaction = Transaction(fromId = account.id, toId = account.id, amount = 1.0, currency = "EUR")
        //Test will pass only if validation failed and api returned 'BAD REQUEST'
        val badRequest = SC_BAD_REQUEST
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            SC_BAD_REQUEST,
            handler = Handler {
                val message = it.body().toString()
                assert(message.contains("same account"))
                context.completeNow()
            })
    }

    @Test
    fun `Should fail create transaction with non existing account`(
        info: TestInfo,
        vertx: Vertx,
        context: VertxTestContext
                                                                  ) {
        val account = accounts.first()
        val randomStringAsId = "RANDOM"
        val transaction = Transaction(fromId = randomStringAsId, toId = account.id, amount = 1.0, currency = "EUR")
        //Test will pass only if validation failed and api returned 'BAD REQUEST'
        val badRequest = SC_BAD_REQUEST
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            badRequest,
            handler = Handler {
                val message = it.body().toString()
                assert(message.contains(randomStringAsId))
                context.completeNow()
            })
    }

    @Test
    fun `Should fail create transaction with non valid currency`(
        info: TestInfo,
        vertx: Vertx,
        context: VertxTestContext
                                                                ) {
        val account = accounts.first()
        val currency = "RANDOM"
        val transaction = Transaction(fromId = account.id, toId = account.toString(), amount = 1.0, currency = currency)
        //Test will pass only if validation failed and api returned 'BAD REQUEST'
        val badRequest = SC_BAD_REQUEST
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            badRequest,
            handler = Handler {
                val message = it.body().toString()
                assert(message.contains("currency"))
                context.completeNow()
            })
    }

    @Test
    fun `Should fail create transaction invalid amount`(info: TestInfo, vertx: Vertx, context: VertxTestContext) {
        val account = accounts.first()
        val amount = 0.0
        val transaction = Transaction(fromId = account.id, toId = account.toString(), amount = amount, currency = "EUR")
        //Test will pass only if validation failed and api returned 'BAD REQUEST'
        val badRequest = SC_BAD_REQUEST
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            badRequest,
            handler = Handler {
                val message = it.body().toString()
                assert(message.contains("amount"))
                context.completeNow()
            })
    }

    @Test
    fun `Should fail create transaction with not enough balance`(
        info: TestInfo,
        vertx: Vertx,
        context: VertxTestContext
                                                                ) {
        val sender = accounts.first()
        val receiver = accounts.last()
        val senderBalance = sender.balance
        val transaction =
            Transaction(fromId = sender.id, toId = receiver.id, amount = senderBalance + 1000.0, currency = "EUR")
        //Test will pass only if validation failed and api returned 'BAD REQUEST'
        val badRequest = SC_BAD_REQUEST
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            badRequest,
            handler = Handler {
                val message = it.body().toString()
                print(message)
                assert(message.contains("Sender balance is not enough for performing this operation"))
                context.completeNow()
            })
    }

    //Negative case block end


    @Test
    fun `Should  create transaction`(info: TestInfo, vertx: Vertx, context: VertxTestContext) {
        val sender = accounts.first()
        val receiver = accounts.last()
        val amount = sender.balance
        val transaction = Transaction(fromId = sender.id, toId = receiver.id, amount = amount, currency = sender.currency)
        sendRequestAndAssert(vertx,
            context,
            transaction.toJson(),
            HttpMethod.POST,
            TRANSACTION_API_PATH,
            handler = Handler {
                val createdTransaction = it.body().toJsonObject().mapTo(Transaction::class.java)
                assert(createdTransaction == transaction)
                context.completeNow()
            })
    }



}