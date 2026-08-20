package com.example.routing

import com.example.model.CreateSpecialistRequest
import com.example.model.UpdateSpecialistRequest
import com.example.repository.SpecialistRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSpecialistRouting(repository: SpecialistRepository) {

    routing {
        post("/specialists") {
            val request = call.receive<CreateSpecialistRequest>()
            val specialist = repository.create(
                firstName = request.firstName,
                lastName = request.lastName,
                profession = request.profession
            )
            call.respond(HttpStatusCode.Created, specialist)
        }

        get("/specialists") {
            val specialists = repository.getAll()
            call.respond(HttpStatusCode.OK, specialists)
        }

        get("/specialists/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val specialist = repository.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, specialist)
        }

        put("/specialists/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val request = call.receive<UpdateSpecialistRequest>()
            val specialist = repository.update(
                id = id,
                firstName = request.firstName,
                lastName = request.lastName,
                profession = request.profession
            ) ?: return@put call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, specialist)
        }

        delete("/specialists/{id}") {
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

