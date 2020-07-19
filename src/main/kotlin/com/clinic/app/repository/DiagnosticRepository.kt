package com.clinic.app.repository

import com.clinic.app.models.Diagnostic
import com.clinic.app.models.Diagnostics
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DiagnosticRepository {

    @Throws(ExposedSQLException::class)
    fun createDiagnostic(diagnostic: Diagnostic) {
        transaction {
            Diagnostics.insert {
                it[name] = diagnostic.name
                it[category] = diagnostic.category
            }
        }
    }

    fun getDiagnostic(diagnosticId: Int): Diagnostic? {
        return transaction {
            Diagnostics.select { Diagnostics.id eq diagnosticId }
                .map { toDiagnostic(it) }.firstOrNull()

        }
    }

    @Throws(ExposedSQLException::class)
    fun updateDiagnostic(diagnosticId: Int, updatedDiagnostic: Diagnostic) {
        transaction {
            Diagnostics.update({Diagnostics.id eq diagnosticId}) {
                it[Diagnostics.name] = updatedDiagnostic.name
                it[Diagnostics.category] = updatedDiagnostic.category
            }
        }
    }

    fun deleteDiagnostic(diagnosticId: Int) {
        transaction {
            Diagnostics.deleteWhere { Diagnostics.id eq diagnosticId }
        }
    }

    fun getAll(): List<Diagnostic> {
        return transaction {
            Diagnostics.selectAll().map { toDiagnostic(it) }
        }
    }

    private fun toDiagnostic(row: ResultRow): Diagnostic =
        Diagnostic(
            id = row[Diagnostics.id],
            name = row[Diagnostics.name],
            category = row[Diagnostics.category]
        )
}