package com.example.routing

import com.example.routing.configureAppointmentRouting
import com.example.configureSerialization
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.repository.AppointmentRepository
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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

    private val appointment1 = Appointment(
        id = 1,
        description = "Learn Ktor",
        startTime = "2026-08-19 09:00:00",
        endTime = "2026-08-19 10:00:00",
        status = AppointmentStatus.RESERVED
    )

    @Test
    fun `POST appointments creates an appointment`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every {
            repository.create(
                description = "Learn Ktor",
                startTime = "2026-08-19 09:00:00",
                endTime = "2026-08-19 10:00:00",
                status = AppointmentStatus.RESERVED,
                clientId = null,
                specialistId = null
            )
        } returns appointment1

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.post("/appointments") {
            contentType(ContentType.Application.Json)
            setBody("""{"description":"Learn Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"reserved"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"id":1,"description":"Learn Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"reserved","clientId":null,"specialistId":null,"client":null,"specialist":null}""",
            response.bodyAsText()
        )

        verify {
            repository.create(
                description = "Learn Ktor",
                startTime = "2026-08-19 09:00:00",
                endTime = "2026-08-19 10:00:00",
                status = AppointmentStatus.RESERVED,
                clientId = null,
                specialistId = null
            )
        }
    }

    @Test
    fun `GET appointments returns all appointments`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.getAll() } returns listOf(
            appointment1,
            Appointment(
                id = 2,
                description = "Write tests",
                startTime = "2026-08-19 11:00:00",
                endTime = "2026-08-19 12:00:00",
                status = AppointmentStatus.DONE
            )
        )

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """[{"id":1,"description":"Learn Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"reserved","clientId":null,"specialistId":null,"client":null,"specialist":null},{"id":2,"description":"Write tests","startTime":"2026-08-19 11:00:00","endTime":"2026-08-19 12:00:00","status":"done","clientId":null,"specialistId":null,"client":null,"specialist":null}]""",
            response.bodyAsText()
        )

        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `GET appointment by id returns appointment when found`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.getById(1) } returns appointment1

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"description":"Learn Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"reserved","clientId":null,"specialistId":null,"client":null,"specialist":null}""",
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
    fun `GET appointment by id returns bad request when id is invalid`() = testApplication {
        val repository = mockk<AppointmentRepository>(relaxed = true)

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.get("/appointments/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)

        verify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `PUT appointment updates and returns appointment`() = testApplication {
        val repository = mockk<AppointmentRepository>()
        val updated = appointment1.copy(description = "Updated Ktor", status = AppointmentStatus.DONE)

        every {
            repository.update(
                id = 1,
                description = "Updated Ktor",
                startTime = "2026-08-19 09:00:00",
                endTime = "2026-08-19 10:00:00",
                status = AppointmentStatus.DONE,
                clientId = null,
                specialistId = null
            )
        } returns updated

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.put("/appointments/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"description":"Updated Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"done"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"description":"Updated Ktor","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"done","clientId":null,"specialistId":null,"client":null,"specialist":null}""",
            response.bodyAsText()
        )
    }

    @Test
    fun `PUT appointment returns not found when appointment does not exist`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every {
            repository.update(
                id = 999,
                description = any(),
                startTime = any(),
                endTime = any(),
                status = any(),
                clientId = any(),
                specialistId = any()
            )
        } returns null

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.put("/appointments/999") {
            contentType(ContentType.Application.Json)
            setBody("""{"description":"X","startTime":"2026-08-19 09:00:00","endTime":"2026-08-19 10:00:00","status":"reserved"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE appointment returns no content when deleted`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.delete(1) } returns true

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.delete("/appointments/1")

        assertEquals(HttpStatusCode.NoContent, response.status)
        verify(exactly = 1) { repository.delete(1) }
    }

    @Test
    fun `DELETE appointment returns not found when appointment does not exist`() = testApplication {
        val repository = mockk<AppointmentRepository>()

        every { repository.delete(999) } returns false

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.delete("/appointments/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify(exactly = 1) { repository.delete(999) }
    }

    @Test
    fun `DELETE appointment returns bad request when id is invalid`() = testApplication {
        val repository = mockk<AppointmentRepository>(relaxed = true)

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        val response = client.delete("/appointments/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        verify(exactly = 0) { repository.delete(any()) }
    }
}