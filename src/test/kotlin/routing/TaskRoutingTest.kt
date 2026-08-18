package com.example.routing

import com.example.Routing.configureRouting
import com.example.configureSerialization
import com.example.model.Task
import com.example.repository.TaskRepository
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import kotlin.test.*

class TaskRoutingTest {
    @Test
    fun `POST tasks creates a task`() = testApplication {
        val repository = mockk<TaskRepository>()

        every {
            repository.create("Learn Ktor", false)
        } returns Task(
            id = 1,
            done = false,
            description = "Learn Ktor"
        )

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"description":"Learn Ktor", "done": false}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"id":1,"description":"Learn Ktor","done":false}""",
            response.bodyAsText()
        )

        verify {
            repository.create("Learn Ktor", false)
        }
    }
}