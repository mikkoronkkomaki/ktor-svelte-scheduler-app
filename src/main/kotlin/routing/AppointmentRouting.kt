package com.example.routing

import com.example.model.CreateAppointmentRequest
import com.example.repository.AppointmentRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAppointmentRouting(repository: AppointmentRepository) {

    routing {
        post("/appointments") {
            val request = call.receive<CreateAppointmentRequest>()
            val appointment = repository.create(
                description = request.description,
                startTime = request.startTime,
                endTime = request.endTime,
                status = request.status,
                clientId = request.clientId,
                specialistId = request.specialistId
            )
            call.respond(HttpStatusCode.Created, appointment)
        }

        get("/appointments") {
            val appointments = repository.getAll()
            call.respond(HttpStatusCode.Accepted, appointments)
        }

        get("/appointments/{id}") {
            val appointmentId = call.parameters["id"]?.toIntOrNull()
            if (appointmentId != null) {
                val appointment = repository.getById(appointmentId)
                if (appointment != null) {
                    call.respond(HttpStatusCode.Found, appointment)
                    return@get
                }
            }
            call.respond(HttpStatusCode.NotFound)
        }
    }
}