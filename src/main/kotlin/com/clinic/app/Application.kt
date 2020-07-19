package com.clinic.app

import com.clinic.app.repository.ClinicRepository
import com.clinic.app.repository.DiagnosticRepository
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        gson {
        }
    }
    install(DefaultHeaders)
    install(CallLogging) {}

    DatabaseFactory.init()

    val clinicRepository = ClinicRepository()
    val diagnosticRepository = DiagnosticRepository()

    install(Routing) {
        clinic(clinicRepository)
        diagnostic(diagnosticRepository)
    }
}

@KtorExperimentalAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, 8080, watchPaths = listOf("clinic", "diagnostic"), module = Application::module).start(wait = true)
}

