package com.clinic.app

import com.clinic.app.models.Clinic
import com.clinic.app.repository.ClinicRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.lang.Exception

fun Route.clinic(clinicRepository: ClinicRepository) {
    route("/clinics") {

        get("/") {
            val clinics: List<Clinic> = clinicRepository.getAll()
            call.respond(HttpStatusCode.OK, clinics)

        }

        get("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            when (val clinic = clinicRepository.getClinic(id.toInt())) {
                null -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.OK, clinic)
            }
        }

        post("/new") {
            val clinic = call.receive<Clinic>()
            try {
                clinicRepository.createClinic(clinic)
                call.respond(HttpStatusCode.OK, clinic)
            } catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }

        put(path = "/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            val diagnostic = call.receive<Clinic>()
            try {
                clinicRepository.updateClinic(id.toInt(), diagnostic)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }

        delete(path = "/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            clinicRepository.deleteClinic(id.toInt())
            call.respond(HttpStatusCode.NoContent)
        }
    }
}