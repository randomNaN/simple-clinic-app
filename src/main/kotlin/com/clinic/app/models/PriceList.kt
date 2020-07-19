package com.clinic.app.models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object PriceList: Table("price_list") {
    val clinic_id: Column<Int> = integer("clinic_id").references(Clinics.id)
    val diagnostic_id: Column<Int> = integer("diagnostic_id").references(Diagnostics.id)
    val price: Column<Int> = integer("price")

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(clinic_id, diagnostic_id)
}

data class ServiceDTO(val diagnostic_id: Int, val price: Int)