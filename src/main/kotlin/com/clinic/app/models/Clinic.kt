package com.clinic.app.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Clinics: Table("clinic") {
    val id: Column<Int> = integer("clinic_id").autoIncrement()
    val name: Column<String> = varchar("clinic_name", 60)
    val addr: Column<String> = varchar("clinic_addr", 100)
    val phone: Column<String> = varchar("clinic_phone", 11)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(id)
}

data class Clinic(
    val id: Int,
    val name: String,
    val addr: String,
    val phone: String,
    val services: ArrayList<ServiceDTO>
)

data class ClinicSimple(
        val id: Int,
        val name: String,
        val addr: String,
        val phone: String
)

