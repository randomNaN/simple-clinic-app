package com.clinic.app.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Diagnostics: Table("diagnostic") {
    val id: Column<Int> = integer("diagnostic_id").autoIncrement()
    val name: Column<String> = varchar("diagnostic_name", 60)
    val category: Column<String> = varchar("diagnostic_cat", 60)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}
data class Diagnostic(
    val id: Int,
    val name: String,
    val category: String
)