package com.example.routing

import com.example.routing.configureAppointmentRouting
import com.example.configureSerialization
import com.example.model.Appointment
import com.example.repository.AppointmentRepository
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

class AppointmentRoutingTest {
    @Test
    fun `POST appointments creates an appointment`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every {
            repository.create("Learn Ktor", false)
        } returns Appointment(
            id = 1,
            done = false,
            description = "Learn Ktor"
        )

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.post("/appointments") {
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
    fun `GET appointments returns all appointments`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.getAll() } returns listOf(
            Appointment(id = 1, description = "Learn Ktor", done = false),
            Appointment(id = 2, description = "Write tests", done = true)
        )

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments")

        assertEquals(HttpStatusCode.Accepted, response.status)
        assertEquals(
            """[{"id":1,"description":"Learn Ktor","done":false},{"id":2,"description":"Write tests","done":true}]""",
            response.bodyAsText()
        )

        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `GET appointment by id returns appointment when found`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.getById(1) } returns Appointment(
            id = 1,
            description = "Learn Ktor",
            done = false
        )

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments/1")

        assertEquals(HttpStatusCode.Found, response.status)
        assertEquals(
            """{"id":1,"description":"Learn Ktor","done":false}""",
            response.bodyAsText()
        )

        verify(exactly = 1) { repository.getById(1) }
    }

    @Test
    fun `GET appointment by id returns not found when appointment does not exist`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.getById(999) } returns null

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments/999")

        assertEquals(HttpStatusCode.NotFound, response.status)

        verify(exactly = 1) { repository.getById(999) }
    }

    @Test
    fun `GET appointment by id returns not found when id is invalid`() = testApplication {
        val repository = mockk<AppointmentRepository>(relaxed = true)

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments/not-a-number")

        assertEquals(HttpStatusCode.NotFound, response.status)

        verify(exactly = 0) { repository.getById(any()) }
    }
}