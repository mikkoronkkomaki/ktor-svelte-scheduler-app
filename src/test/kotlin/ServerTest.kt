package com.example

import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.repository.AppointmentRepository
import com.example.routing.configureAppointmentRouting
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import kotlin.test.*

class ServerTest {

    @Test
    fun `test appointments endpoint`() = testApplication {
        val repository = mockk<AppointmentRepository>()
        every { repository.getAll() } returns listOf(
            Appointment(
                id = 1,
                description = "Learn Ktor",
                startTime = "2026-08-19 09:00:00",
                endTime = "2026-08-19 10:00:00",
                status = AppointmentStatus.RESERVED
            )
        )

        application {
            configureSerialization()
            configureAppointmentRouting(repository)
        }

        assertEquals(HttpStatusCode.Accepted, client.get("/appointments").status)
    }

}
