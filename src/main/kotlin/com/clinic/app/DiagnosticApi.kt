package com.clinic.app

import com.clinic.app.models.Diagnostic
import com.clinic.app.repository.DiagnosticRepository
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import java.lang.Exception

fun Route.diagnostic(diagnosticRepository: DiagnosticRepository) {

    route("/diagnostics") {
        get("/") {
            val items: List<Diagnostic> = diagnosticRepository.getAll()
            call.respond(HttpStatusCode.OK, items)
        }

        post("/new") {
            val diagnostic = call.receive<Diagnostic>()
            try {
                diagnosticRepository.createDiagnostic(diagnostic)
                call.respond(HttpStatusCode.OK, diagnostic)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            when (val diagnostic = diagnosticRepository.getDiagnostic(id.toInt())) {
                null -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.OK, diagnostic)
            }
        }

        put(path = "/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            val diagnostic = call.receive<Diagnostic>()
            try {
                diagnosticRepository.updateDiagnostic(id.toInt(), diagnostic)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.message.toString())
            }
        }

        delete(path = "/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
            diagnosticRepository.deleteDiagnostic(id.toInt())
            call.respond(HttpStatusCode.NoContent)
        }
    }
}