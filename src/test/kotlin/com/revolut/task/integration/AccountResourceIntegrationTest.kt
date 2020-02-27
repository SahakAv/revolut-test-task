package com.revolut.task.integration

import com.revolut.task.config.TestManager
import com.revolut.task.model.Account
import com.revolut.task.util.sendRequestAndAssert
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.core.json.json
import org.apiguardian.api.API
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [VertxExtension::class])
class AccountResourceIntegrationTest {


    companion object {
        private const val ACCOUNT_API_PATH = "/api/account"
        @JvmStatic
        @BeforeAll
        fun setup(vertx: Vertx) {
            TestManager.deployApp(vertx)

        }

        @JvmStatic
        @AfterAll
        fun cleanup(vertx: Vertx) {
            TestManager.stop(vertx)
        }
    }

    @Test
    fun `Should fetch accounts`(info: TestInfo, vertx: Vertx, context: VertxTestContext) {
        sendRequestAndAssert(vertx, context, JsonObject(), HttpMethod.GET, ACCOUNT_API_PATH, handler = Handler {
            val jsonArray = it.body().toJsonArray()
            assert(!jsonArray.isEmpty)
            context.completeNow()
        })
    }

    @Test
    fun `Should create and find account`(info: TestInfo, vertx: Vertx, context: VertxTestContext) {
        val account = Account(ownerName = "John", balance = 50.0, blockedAmount = 0.0, currency = "EUR")
        sendRequestAndAssert(vertx, context, account.toJson(), HttpMethod.POST, ACCOUNT_API_PATH, handler = Handler {
            val createAccount = it.body().toJsonObject().mapTo(Account::class.java)
            sendRequestAndAssert(vertx,
                context,
                JsonObject(),
                HttpMethod.GET,
                appendId(createAccount.id),
                handler = Handler {
                    val fetchedAccount = it.body().toJsonObject().mapTo(Account::class.java)
                    assert(fetchedAccount == createAccount)
                    context.completeNow()
                })

        })
    }

    private fun appendId(id: String): String {
        return "$ACCOUNT_API_PATH?id=$id"
    }

}