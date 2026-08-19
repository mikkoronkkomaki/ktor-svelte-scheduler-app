package com.example.routing

import com.example.Routing.configureRouting
import com.example.configureSerialization
import com.example.model.Task
import com.example.repository.TaskRepository
import io.ktor.client.request.get
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

    @Test
    fun `GET tasks returns all tasks`() = testApplication {
        val repository = mockk<TaskRepository>()

        every { repository.getAll() } returns listOf(
            Task(id = 1, description = "Learn Ktor", done = false),
            Task(id = 2, description = "Write tests", done = true)
        )

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals(
            """[{"id":1,"description":"Learn Ktor","done":false},{"id":2,"description":"Write tests","done":true}]""",
            response.bodyAsText()
        )

        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `GET task by id returns task when found`() = testApplication {
        val repository = mockk<TaskRepository>()

        every { repository.getById(1) } returns Task(
            id = 1,
            description = "Learn Ktor",
            done = false
        )

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks/1")

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals(
            """{"id":1,"description":"Learn Ktor","done":false}""",
            response.bodyAsText()
        )

        verify(exactly = 1) { repository.getById(1) }
    }

    @Test
    fun `GET task by id returns not found when task does not exist`() = testApplication {
        val repository = mockk<TaskRepository>()

        every { repository.getById(999) } returns null

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks/999")

        assertEquals(HttpStatusCode.NotFound, response.status)

        verify(exactly = 1) { repository.getById(999) }
    }

    @Test
    fun `GET task by id returns not found when id is invalid`() = testApplication {
        val repository = mockk<TaskRepository>(relaxed = true)

        application {
            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks/not-a-number")

        assertEquals(HttpStatusCode.NotFound, response.status)

        verify(exactly = 0) { repository.getById(any()) }
    }
}