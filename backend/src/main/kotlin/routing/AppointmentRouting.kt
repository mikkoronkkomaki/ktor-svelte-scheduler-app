package com.example.routing

import com.example.model.CreateAppointmentRequest
import com.example.model.UpdateAppointmentRequest
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
            call.respond(HttpStatusCode.OK, appointments)
        }

        get("/appointments/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val appointment = repository.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, appointment)
        }

        put("/appointments/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val request = call.receive<UpdateAppointmentRequest>()
            val appointment = repository.update(
                id = id,
                description = request.description,
                startTime = request.startTime,
                endTime = request.endTime,
                status = request.status,
                clientId = request.clientId,
                specialistId = request.specialistId
            ) ?: return@put call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, appointment)
        }

        delete("/appointments/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val deleted = repository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}