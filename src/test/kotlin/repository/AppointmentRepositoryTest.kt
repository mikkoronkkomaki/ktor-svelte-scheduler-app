package com.example.repository

import com.example.model.AppointmentStatus
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.*
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Testcontainers
class AppointmentRepositoryTest {
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
    private lateinit var repository: AppointmentRepository

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

        repository = AppointmentRepository(dataSource)
    }

    @AfterEach
    fun tearDown() {
        if (!::dataSource.isInitialized) return

        runCatching {
            dataSource.connection.use { connection ->
                connection.createStatement().use {
                    it.executeUpdate("DELETE FROM scheduler.appointment")
                }
            }
        }

        dataSource.close()
    }
    
    @Test
    fun `create appointment`() {
        val appointment = repository.create(
            description = "KTOR lessons",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.RESERVED,
            clientId = null,
            specialistId = null
        )

        assertIs<Number>(appointment.id)
        assertEquals("KTOR lessons", appointment.description)
        assertEquals(AppointmentStatus.RESERVED, appointment.status)
    }

    @Test
    fun `get all returns created appointments`() {
        val created1 = repository.create(
            description = "KTOR lessons",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.RESERVED,
            clientId = null,
            specialistId = null
        )
        val created2 = repository.create(
            description = "Clojure lessons",
            startTime = "2026-08-19 11:00:00",
            endTime = "2026-08-19 12:00:00",
            status = AppointmentStatus.DONE,
            clientId = null,
            specialistId = null
        )

        val appointments = repository.getAll()

        assertEquals(2, appointments.size)

        val byDescription = appointments.associateBy { it.description }

        val appointment1 = byDescription["KTOR lessons"]
        assertNotNull(appointment1)
        assertEquals(created1.id, appointment1.id)
        assertEquals(AppointmentStatus.RESERVED, appointment1.status)

        val appointment2 = byDescription["Clojure lessons"]
        assertNotNull(appointment2)
        assertEquals(created2.id, appointment2.id)
        assertEquals(AppointmentStatus.DONE, appointment2.status)
    }


    @Test
    fun `get by id returns appointment when it exists`() {
        val created = repository.create(
            description = "Find me",
            startTime = "2026-08-19 13:00:00",
            endTime = "2026-08-19 14:00:00",
            status = AppointmentStatus.CANCELLED,
            clientId = null,
            specialistId = null
        )

        val found = repository.getById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Find me", found.description)
        assertEquals(AppointmentStatus.CANCELLED, found.status)
    }
    
    @Test
    fun `get by id returns null when not found`() {
        val appointment = repository.getById(-1)
        assertNull(appointment)
    }

    @Test
    fun `update modifies appointment fields`() {
        val created = repository.create(
            description = "Original",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.RESERVED,
            clientId = null,
            specialistId = null
        )

        val updated = repository.update(
            id = created.id,
            description = "Updated",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.DONE,
            clientId = null,
            specialistId = null
        )

        assertNotNull(updated)
        assertEquals(created.id, updated.id)
        assertEquals("Updated", updated.description)
        assertEquals(AppointmentStatus.DONE, updated.status)
    }

    @Test
    fun `update returns null when appointment does not exist`() {
        val result = repository.update(
            id = -1,
            description = "X",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.RESERVED,
            clientId = null,
            specialistId = null
        )
        assertNull(result)
    }

    @Test
    fun `delete removes appointment and returns true`() {
        val created = repository.create(
            description = "To be deleted",
            startTime = "2026-08-19 09:00:00",
            endTime = "2026-08-19 10:00:00",
            status = AppointmentStatus.RESERVED,
            clientId = null,
            specialistId = null
        )

        val deleted = repository.delete(created.id)

        assertTrue(deleted)
        assertNull(repository.getById(created.id))
    }

    @Test
    fun `delete returns false when appointment does not exist`() {
        val deleted = repository.delete(-1)
        assertEquals(false, deleted)
    }

}