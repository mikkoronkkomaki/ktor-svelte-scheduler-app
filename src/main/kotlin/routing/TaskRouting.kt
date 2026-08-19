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
            val task = repository.create(request.description, request.done)
            call.respond(HttpStatusCode.Created, task)
        }

        get("/tasks") {
            val tasks = repository.getAll()
            call.respond(HttpStatusCode.Accepted, tasks)
        }

        get("/tasks/{id}") {
            val taskId = call.parameters["id"]?.toIntOrNull()
            if (taskId != null) {
                val task = repository.getById(taskId)
                if (task != null) {
                    call.respond(HttpStatusCode.Found, task)
                }
            }
            call.respond(HttpStatusCode.NotFound)
        }
    }
}