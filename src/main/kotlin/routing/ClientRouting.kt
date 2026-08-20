package com.example.routing

import com.example.model.CreateClientRequest
import com.example.model.UpdateClientRequest
import com.example.repository.ClientRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureClientRouting(repository: ClientRepository) {

    routing {
        post("/clients") {
            val request = call.receive<CreateClientRequest>()
            val client = repository.create(
                firstName = request.firstName,
                lastName = request.lastName
            )
            call.respond(HttpStatusCode.Created, client)
        }

        get("/clients") {
            val clients = repository.getAll()
            call.respond(HttpStatusCode.OK, clients)
        }

        get("/clients/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val client = repository.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, client)
        }

        put("/clients/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid id")
            val request = call.receive<UpdateClientRequest>()
            val client = repository.update(
                id = id,
                firstName = request.firstName,
                lastName = request.lastName
            ) ?: return@put call.respond(HttpStatusCode.NotFound)
            call.respond(HttpStatusCode.OK, client)
        }

        delete("/clients/{id}") {
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

