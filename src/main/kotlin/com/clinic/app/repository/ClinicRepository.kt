package com.clinic.app.repository

import com.clinic.app.models.*
import com.clinic.app.utils.batchInsertOnDuplicateKeyUpdate
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ClinicRepository {

    fun getAll(): List<Clinic> {
        val clinics = ArrayList<Clinic>()
        val services = ArrayList<ServiceDTO>()
        val ids = HashSet<Int>()
        transaction {
            PriceList
                    .join(Clinics, JoinType.INNER, additionalConstraint = {PriceList.clinic_id eq Clinics.id})
                    .slice(Clinics.id, Clinics.name, Clinics.phone, Clinics.addr, PriceList.diagnostic_id, PriceList.price)
                    .selectAll()
                    .orderBy(Clinics.id)
                    .forEach { row ->
                        if (ids.isEmpty()) {
                            ids.add(row[Clinics.id])
                            clinics.add(toClinic(row))
                        }

                        if (ids.contains(row[Clinics.id])) {
                            services.add(ServiceDTO(row[PriceList.diagnostic_id], row[PriceList.price]))
                        } else {
                            clinics.last().services.addAll(services)
                            services.clear()
                            clinics.add(toClinic(row))
                            ids.add(row[Clinics.id])
                            services.add(ServiceDTO(row[PriceList.diagnostic_id], row[PriceList.price]))
                        }
                     }
        }
        return clinics
    }

    fun getAllSimplified(): List<ClinicSimple> {
        return transaction {
            Clinics.selectAll().map { toSimplifiedClinic(it) }
        }
    }

    fun getClinic(clinicId: Int): Clinic? {
        return transaction {
            val clinic = Clinics.select { Clinics.id eq clinicId }
                    .map { toClinic(it) }.firstOrNull()

            if (clinic != null) {
                val services = PriceList
                        .slice(PriceList.diagnostic_id, PriceList.price)
                        .select { PriceList.clinic_id eq clinicId }
                        .map { toServiceDTO(it) }
                clinic.services.addAll(services)
            }
            return@transaction clinic
        }
    }

    @Throws(ExposedSQLException::class)
    fun createClinic(clinic: Clinic) {
        transaction {
            val id = Clinics.insert {
                it[name] = clinic.name
                it[addr] = clinic.addr
                it[phone] = clinic.phone
            } get Clinics.id
            if (clinic.services.isNotEmpty()) {
                PriceList.batchInsert(clinic.services) { item ->
                    this[PriceList.clinic_id] = id
                    this[PriceList.diagnostic_id] = item.diagnostic_id
                    this[PriceList.price] = item.price
                }
            }
        }
    }

    @Throws(ExposedSQLException::class)
    fun updateClinic(clinicId: Int, updatedClinic: Clinic) {
        transaction {
            Clinics
                    .select { Clinics.id eq clinicId }
                    .map { toSimplifiedClinic(it) }
                    .firstOrNull()
                    ?: throw Exception("Clinic not found to update")
            Clinics.update {
                it[name] = updatedClinic.name
                it[addr] = updatedClinic.addr
                it[phone] = updatedClinic.phone
            }
            //select existing services, if differs from updated then remove from db
            val exServices = PriceList.select { PriceList.clinic_id eq clinicId }
                    .map { toServiceDTO(it) }
            if (exServices.size != updatedClinic.services.size) {
                val updatedIds = updatedClinic.services.mapTo(HashSet<Int>()) { it -> it.diagnostic_id}
                val idsToDelete = ArrayList<Int>()
                exServices.forEach { service ->
                    if (!updatedIds.contains(service.diagnostic_id)) idsToDelete.add(service.diagnostic_id)
                }
                PriceList.deleteWhere { (PriceList.clinic_id eq clinicId) and (PriceList.diagnostic_id inList idsToDelete) }
            }

            PriceList.batchInsertOnDuplicateKeyUpdate(
                    PriceList.clinic_id,
                    PriceList.diagnostic_id,
                    data = updatedClinic.services,
                    onDupUpdateColumns = listOf(PriceList.price)) {
                batch, service ->
                batch[clinic_id] = clinicId
                batch[diagnostic_id] = service.diagnostic_id
                batch[price] = service.price
            }
        }
    }

    fun deleteClinic(clinicId: Int) {
        transaction {
            Clinics.deleteWhere { Clinics.id eq clinicId }
        }
    }

    private fun toClinic(row: ResultRow): Clinic =
            Clinic(
                    id = row[Clinics.id],
                    name = row[Clinics.name],
                    addr = row[Clinics.addr],
                    phone = row[Clinics.phone],
                    services = arrayListOf()
            )

    private fun toSimplifiedClinic(row: ResultRow): ClinicSimple =
            ClinicSimple(
                    id = row[Clinics.id],
                    name = row[Clinics.name],
                    addr = row[Clinics.addr],
                    phone = row[Clinics.phone]
            )

    private fun toServiceDTO(row: ResultRow): ServiceDTO =
            ServiceDTO(
                    diagnostic_id = row[PriceList.diagnostic_id],
                    price = row[PriceList.price]
            )
}