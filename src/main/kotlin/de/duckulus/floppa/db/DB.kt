package de.duckulus.floppa.db

import de.duckulus.floppa.db.entities.User
import io.github.oshai.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

val logger = KotlinLogging.logger("Database")

object DB {

    init {
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
        User.update({ User.phoneNumber eq phoneNumber }) {
            it[permissionLevel] = newValue
        }
    }


}