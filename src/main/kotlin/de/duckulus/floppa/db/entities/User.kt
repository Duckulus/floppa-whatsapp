package de.duckulus.floppa.db.entities

import org.jetbrains.exposed.sql.Table

object User: Table() {
    val phoneNumber = varchar("phoneNumber", 255)
    val permissionLevel = integer("permissionLevel").default(0)
    override val primaryKey = PrimaryKey(phoneNumber)
}