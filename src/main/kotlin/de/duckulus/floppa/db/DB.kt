package de.duckulus.floppa.db

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import de.duckulus.floppa.db.entities.User
import io.github.oshai.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

val logger = KotlinLogging.logger("Database")

object DB {

    init {
        val hikariLogger = LoggerFactory.getLogger("com.zaxxer.hikari") as Logger
        val hikariConfigLogger = LoggerFactory.getLogger("com.zaxxer.hikari.HikariConfig") as Logger
        hikariLogger.level = Level.INFO
        hikariConfigLogger.level = Level.INFO
        Database.connect(sqliteDataSource("floppa.db"))
        logger.info("Connected to Database")
    }

    fun createTables() {
        transaction {
            SchemaUtils.create(User)
        }
    }

    fun getPermissionLevel(phoneNumber: String): Int = transaction {
        val query = User.select(User.phoneNumber eq phoneNumber)
        if (query.count().toInt() == 0) {
            User.insert {
                it[User.phoneNumber] = phoneNumber
            }
            return@transaction 0
        } else {
            return@transaction query.first()[User.permissionLevel]
        }
    }

    fun setPermissionLevel(phoneNumber: String, newValue: Int) = transaction {
        val query = User.select(User.phoneNumber eq phoneNumber)
        if (query.count().toInt() == 0) {
            User.insert {
                it[User.phoneNumber] = phoneNumber
                it[permissionLevel] = newValue
            }
        } else {
            User.update({ User.phoneNumber eq phoneNumber }) {
                it[permissionLevel] = newValue
            }
        }

    }


}