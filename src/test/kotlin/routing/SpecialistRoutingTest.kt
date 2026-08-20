package com.example.routing

import com.example.configureSerialization
import com.example.model.Specialist
import com.example.repository.SpecialistRepository
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class SpecialistRoutingTest {

    private val specialist1 = Specialist(id = 1, firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")
    private val specialist2 = Specialist(id = 2, firstName = "Jorma", lastName = "Kuikelo", profession = "hammashygienisti")

    @Test
    fun `POST specialists creates a specialist`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every {
            repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")
        } returns specialist1

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.post("/specialists") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"Irmeli","lastName":"Römppönen","profession":"mekaanikko"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"id":1,"firstName":"Irmeli","lastName":"Römppönen","profession":"mekaanikko"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko") }
    }

    @Test
    fun `GET specialists returns all specialists`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.getAll() } returns listOf(specialist1, specialist2)

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.get("/specialists")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """[{"id":1,"firstName":"Irmeli","lastName":"Römppönen","profession":"mekaanikko"},{"id":2,"firstName":"Jorma","lastName":"Kuikelo","profession":"hammashygienisti"}]""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `GET specialist by id returns specialist when found`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.getById(1) } returns specialist1

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.get("/specialists/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"firstName":"Irmeli","lastName":"Römppönen","profession":"mekaanikko"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.getById(1) }
    }

    @Test
    fun `GET specialist by id returns not found when specialist does not exist`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.getById(999) } returns null

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.get("/specialists/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify(exactly = 1) { repository.getById(999) }
    }

    @Test
    fun `GET specialist by id returns bad request when id is invalid`() = testApplication {
        val repository = mockk<SpecialistRepository>(relaxed = true)

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.get("/specialists/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        verify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `PUT specialist updates and returns specialist`() = testApplication {
        val repository = mockk<SpecialistRepository>()
        val updated = specialist1.copy(profession = "sähköasentaja")

        every {
            repository.update(id = 1, firstName = "Irmeli", lastName = "Römppönen", profession = "sähköasentaja")
        } returns updated

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.put("/specialists/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"Irmeli","lastName":"Römppönen","profession":"sähköasentaja"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"firstName":"Irmeli","lastName":"Römppönen","profession":"sähköasentaja"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.update(id = 1, firstName = "Irmeli", lastName = "Römppönen", profession = "sähköasentaja") }
    }

    @Test
    fun `PUT specialist returns not found when specialist does not exist`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.update(id = 999, firstName = any(), lastName = any(), profession = any()) } returns null

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.put("/specialists/999") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"X","lastName":"Y","profession":"Z"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE specialist returns no content when deleted`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.delete(1) } returns true

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.delete("/specialists/1")

        assertEquals(HttpStatusCode.NoContent, response.status)
        verify(exactly = 1) { repository.delete(1) }
    }

    @Test
    fun `DELETE specialist returns not found when specialist does not exist`() = testApplication {
        val repository = mockk<SpecialistRepository>()

        every { repository.delete(999) } returns false

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.delete("/specialists/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify(exactly = 1) { repository.delete(999) }
    }

    @Test
    fun `DELETE specialist returns bad request when id is invalid`() = testApplication {
        val repository = mockk<SpecialistRepository>(relaxed = true)

        application {
            configureSerialization()
            configureSpecialistRouting(repository)
        }

        val response = client.delete("/specialists/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        verify(exactly = 0) { repository.delete(any()) }
    }
}

