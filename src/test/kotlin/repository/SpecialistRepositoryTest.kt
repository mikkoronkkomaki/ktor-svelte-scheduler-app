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
class SpecialistRepositoryTest {

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
    private lateinit var repository: SpecialistRepository

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

        repository = SpecialistRepository(dataSource)
    }

    @AfterEach
    fun tearDown() {
        if (!::dataSource.isInitialized) return
        runCatching {
            dataSource.connection.use { connection ->
                connection.createStatement().use {
                    it.executeUpdate("DELETE FROM scheduler.specialist")
                }
            }
        }
        dataSource.close()
    }

    @Test
    fun `create specialist`() {
        val specialist = repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")

        assertNotNull(specialist.id)
        assertEquals("Irmeli", specialist.firstName)
        assertEquals("Römppönen", specialist.lastName)
        assertEquals("mekaanikko", specialist.profession)
    }

    @Test
    fun `get all returns all created specialists`() {
        val created1 = repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")
        val created2 = repository.create(firstName = "Jorma", lastName = "Kuikelo", profession = "hammashygienisti")

        val specialists = repository.getAll()

        assertEquals(2, specialists.size)
        val byId = specialists.associateBy { it.id }
        assertNotNull(byId[created1.id])
        assertNotNull(byId[created2.id])
    }

    @Test
    fun `get by id returns specialist when it exists`() {
        val created = repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")

        val found = repository.getById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Irmeli", found.firstName)
        assertEquals("Römppönen", found.lastName)
        assertEquals("mekaanikko", found.profession)
    }

    @Test
    fun `get by id returns null when not found`() {
        val specialist = repository.getById(-1)
        assertNull(specialist)
    }

    @Test
    fun `update modifies specialist fields`() {
        val created = repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")

        val updated = repository.update(
            id = created.id,
            firstName = "Irmeli",
            lastName = "Römppönen",
            profession = "sähköasentaja"
        )

        assertNotNull(updated)
        assertEquals(created.id, updated.id)
        assertEquals("sähköasentaja", updated.profession)
    }

    @Test
    fun `update returns null when specialist does not exist`() {
        val result = repository.update(id = -1, firstName = "X", lastName = "Y", profession = "Z")
        assertNull(result)
    }

    @Test
    fun `delete removes specialist and returns true`() {
        val created = repository.create(firstName = "Irmeli", lastName = "Römppönen", profession = "mekaanikko")

        val deleted = repository.delete(created.id)

        assertTrue(deleted)
        assertNull(repository.getById(created.id))
    }

    @Test
    fun `delete returns false when specialist does not exist`() {
        val deleted = repository.delete(-1)
        assertEquals(false, deleted)
    }
}

