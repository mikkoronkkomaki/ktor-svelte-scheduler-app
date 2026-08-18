package com.example.Routing

import com.example.model.CreateTaskRequest
import com.example.repository.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(repository: TaskRepository) {

    routing {
        post("/tasks") {
            val request = call.receive<CreateTaskRequest>()
            log.info("---->>> request: $request")
            val task = repository.create(request.description, request.done)
            call.respond(HttpStatusCode.Created, task)
        }
    }
}