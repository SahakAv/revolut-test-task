package com.revolut.task.db

import com.revolut.task.config.ConfigConstants
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.DriverManager

class DatabaseMigrationVerticle : CoroutineVerticle() {

    override suspend fun start() {

        val dbUrl = config.getString(ConfigConstants.DB_URL)
        val connection = DriverManager.getConnection(dbUrl)

        val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection));
        val liquibase = Liquibase(LIQUIBASE_MASTER_PATH, ClassLoaderResourceAccessor(), database)
        liquibase.update("")
        print("DB migration done")
    }

    companion object {
        private const val LIQUIBASE_MASTER_PATH = "db/migration/changelog.xml"
    }
}
