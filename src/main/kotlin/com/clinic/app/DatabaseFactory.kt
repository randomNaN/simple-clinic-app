package com.clinic.app

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.HoconApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import java.util.*

@KtorExperimentalAPI
object DatabaseFactory {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl = appConfig.property("postgres.dataSource.url").getString()
    private val dbUser = appConfig.property("postgres.dataSource.user").getString()
    private val dbPassword = appConfig.property("postgres.dataSource.password").getString()

    fun init() {
        Database.connect(hikari())
        dbMigrate()
    }


    private fun hikari(): HikariDataSource {
        val dataSource = appConfig.property("postgres.dataSourceClassName").getString()
        val props = Properties()
        props.setProperty("dataSourceClassName", dataSource)
        props.setProperty("dataSource.user", dbUser)
        props.setProperty("dataSource.password", dbPassword)
        props.setProperty("dataSource.databaseName", "clinicdb")
        props.setProperty("dataSource.reWriteBatchedInserts", "true")
        val config = HikariConfig(props)
        return HikariDataSource(config)
    }

    private fun dbMigrate() {
        val flyway = Flyway()
        flyway.setDataSource(dbUrl, dbUser, dbPassword)
        flyway.setLocations("/db/migrations")
        flyway.migrate()
    }
}