package com.example.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Testcontainers
class ClientRepositoryTest {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
            .apply {
                withDatabaseName("testdb")
                withUsername("test")
                withPassword("test")
            }
    }

    private lateinit var dataSource: HikariDataSource
    private lateinit var repository: ClientRepository

    @BeforeEach
    fun setUp() {
        val config = HikariConfig().apply {
            jdbcUrl = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
            driverClassName = "org.postgresql.Driver"
        }
        dataSource = HikariDataSource(config)

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("filesystem:dev-tools/database/flyway")
            .schemas("scheduler")
            .cleanDisabled(false)
            .load()

        flyway.clean()
        flyway.migrate()

        repository = ClientRepository(dataSource)
    }

    @AfterEach
    fun tearDown() {
        if (!::dataSource.isInitialized) return
        runCatching {
            dataSource.connection.use { connection ->
                connection.createStatement().use {
                    it.executeUpdate("DELETE FROM scheduler.client")
                }
            }
        }
        dataSource.close()
    }

    @Test
    fun `create client`() {
        val client = repository.create(firstName = "Maija", lastName = "Virtanen")

        assertNotNull(client.id)
        assertEquals("Maija", client.firstName)
        assertEquals("Virtanen", client.lastName)
    }

    @Test
    fun `get all returns all created clients`() {
        val created1 = repository.create(firstName = "Maija", lastName = "Virtanen")
        val created2 = repository.create(firstName = "Pekka", lastName = "Korhonen")

        val clients = repository.getAll()

        assertEquals(2, clients.size)
        val byId = clients.associateBy { it.id }
        assertNotNull(byId[created1.id])
        assertNotNull(byId[created2.id])
    }

    @Test
    fun `get by id returns client when it exists`() {
        val created = repository.create(firstName = "Maija", lastName = "Virtanen")

        val found = repository.getById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Maija", found.firstName)
        assertEquals("Virtanen", found.lastName)
    }

    @Test
    fun `get by id returns null when not found`() {
        val client = repository.getById(-1)
        assertNull(client)
    }

    @Test
    fun `update modifies client fields`() {
        val created = repository.create(firstName = "Maija", lastName = "Virtanen")

        val updated = repository.update(id = created.id, firstName = "Maija-Liisa", lastName = "Mäkinen")

        assertNotNull(updated)
        assertEquals(created.id, updated.id)
        assertEquals("Maija-Liisa", updated.firstName)
        assertEquals("Mäkinen", updated.lastName)
    }

    @Test
    fun `update returns null when client does not exist`() {
        val result = repository.update(id = -1, firstName = "X", lastName = "Y")
        assertNull(result)
    }

    @Test
    fun `delete removes client and returns true`() {
        val created = repository.create(firstName = "Maija", lastName = "Virtanen")

        val deleted = repository.delete(created.id)

        assertTrue(deleted)
        assertNull(repository.getById(created.id))
    }

    @Test
    fun `delete returns false when client does not exist`() {
        val deleted = repository.delete(-1)
        assertEquals(false, deleted)
    }
}

