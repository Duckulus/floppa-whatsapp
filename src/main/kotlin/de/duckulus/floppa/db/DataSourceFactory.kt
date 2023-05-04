package de.duckulus.floppa.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun sqliteDataSource(filename: String): DataSource {
    val config = HikariConfig()
    config.jdbcUrl = "jdbc:sqlite:./$filename"
    return HikariDataSource(config)
}