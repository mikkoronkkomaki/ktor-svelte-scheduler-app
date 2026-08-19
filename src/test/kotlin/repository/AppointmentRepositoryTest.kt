package com.example.repository

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
        val appointment = repository.create("Learn Ktor", false)

        assertIs<Number>(appointment.id)
        assertEquals("Learn Ktor", appointment.description)
    }

    @Test
    fun `get all returns created appointments`() {
        val created1 = repository.create("Learn Ktor", false)
        val created2 = repository.create("Write tests", true)

        val appointments = repository.getAll()

        assertEquals(2, appointments.size)

        val byDescription = appointments.associateBy { it.description }

        val appointment1 = byDescription["Learn Ktor"]
        assertNotNull(appointment1)
        assertEquals(created1.id, appointment1.id)
        assertEquals(false, appointment1.done)

        val appointment2 = byDescription["Write tests"]
        assertNotNull(appointment2)
        assertEquals(created2.id, appointment2.id)
        assertEquals(true, appointment2.done)
    }


    @Test
    fun `get by id returns appointment when it exists`() {
        val created = repository.create("Find me", false)

        val found = repository.getById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Find me", found.description)
        assertEquals(false, found.done)
    }
    
    @Test
    fun `get by id returns null when not found`() {
        val appointment = repository.getById(-1)
        assertNull(appointment)
    }

}