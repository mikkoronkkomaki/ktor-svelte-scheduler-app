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
class TaskRepositoryTest {
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
    private lateinit var repository: TaskRepository

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
            .schemas("todo")
            .cleanDisabled(false)
            .load()

        flyway.clean()
        flyway.migrate()

        repository = TaskRepository(dataSource)
    }

    @AfterEach
    fun tearDown() {
        if (!::dataSource.isInitialized) return

        runCatching {
            dataSource.connection.use { connection ->
                connection.createStatement().use {
                    it.executeUpdate("DELETE FROM todo.task")
                }
            }
        }

        dataSource.close()
    }
    
    @Test
    fun `create task`() {
        val task = repository.create("Learn Ktor", false)

        assertIs<Number>(task.id)
        assertEquals("Learn Ktor", task.description)
    }

    @Test
    fun `get all returns created tasks`() {
        val created1 = repository.create("Learn Ktor", false)
        val created2 = repository.create("Write tests", true)

        val tasks = repository.getAll()

        assertEquals(2, tasks.size)

        val byDescription = tasks.associateBy { it.description }

        val task1 = byDescription["Learn Ktor"]
        assertNotNull(task1)
        assertEquals(created1.id, task1.id)
        assertEquals(false, task1.done)

        val task2 = byDescription["Write tests"]
        assertNotNull(task2)
        assertEquals(created2.id, task2.id)
        assertEquals(true, task2.done)
    }


    @Test
    fun `get by id returns task when it exists`() {
        val created = repository.create("Find me", false)

        val found = repository.getById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Find me", found.description)
        assertEquals(false, found.done)
    }
    
    @Test
    fun `get by id returns null when not found`() {
        val task = repository.getById(-1)
        assertNull(task)
    }

}