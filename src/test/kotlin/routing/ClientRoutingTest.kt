package com.example.routing

import com.example.configureSerialization
import com.example.model.Client
import com.example.repository.ClientRepository
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

class ClientRoutingTest {

    private val client1 = Client(id = 1, firstName = "Maija", lastName = "Virtanen")
    private val client2 = Client(id = 2, firstName = "Pekka", lastName = "Korhonen")

    @Test
    fun `POST clients creates a client`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.create(firstName = "Maija", lastName = "Virtanen") } returns client1

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.post("/clients") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"Maija","lastName":"Virtanen"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"id":1,"firstName":"Maija","lastName":"Virtanen"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.create(firstName = "Maija", lastName = "Virtanen") }
    }

    @Test
    fun `GET clients returns all clients`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.getAll() } returns listOf(client1, client2)

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.get("/clients")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """[{"id":1,"firstName":"Maija","lastName":"Virtanen"},{"id":2,"firstName":"Pekka","lastName":"Korhonen"}]""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.getAll() }
    }

    @Test
    fun `GET client by id returns client when found`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.getById(1) } returns client1

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.get("/clients/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"firstName":"Maija","lastName":"Virtanen"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.getById(1) }
    }

    @Test
    fun `GET client by id returns not found when client does not exist`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.getById(999) } returns null

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.get("/clients/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify(exactly = 1) { repository.getById(999) }
    }

    @Test
    fun `GET client by id returns bad request when id is invalid`() = testApplication {
        val repository = mockk<ClientRepository>(relaxed = true)

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.get("/clients/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        verify(exactly = 0) { repository.getById(any()) }
    }

    @Test
    fun `PUT client updates and returns client`() = testApplication {
        val repository = mockk<ClientRepository>()
        val updated = client1.copy(firstName = "Maija-Liisa")

        every { repository.update(id = 1, firstName = "Maija-Liisa", lastName = "Virtanen") } returns updated

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.put("/clients/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"Maija-Liisa","lastName":"Virtanen"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            """{"id":1,"firstName":"Maija-Liisa","lastName":"Virtanen"}""",
            response.bodyAsText()
        )
        verify(exactly = 1) { repository.update(id = 1, firstName = "Maija-Liisa", lastName = "Virtanen") }
    }

    @Test
    fun `PUT client returns not found when client does not exist`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.update(id = 999, firstName = any(), lastName = any()) } returns null

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.put("/clients/999") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstName":"X","lastName":"Y"}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `DELETE client returns no content when deleted`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.delete(1) } returns true

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.delete("/clients/1")

        assertEquals(HttpStatusCode.NoContent, response.status)
        verify(exactly = 1) { repository.delete(1) }
    }

    @Test
    fun `DELETE client returns not found when client does not exist`() = testApplication {
        val repository = mockk<ClientRepository>()

        every { repository.delete(999) } returns false

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.delete("/clients/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify(exactly = 1) { repository.delete(999) }
    }

    @Test
    fun `DELETE client returns bad request when id is invalid`() = testApplication {
        val repository = mockk<ClientRepository>(relaxed = true)

        application {
            configureSerialization()
            configureClientRouting(repository)
        }

        val response = client.delete("/clients/not-a-number")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        verify(exactly = 0) { repository.delete(any()) }
    }
}

