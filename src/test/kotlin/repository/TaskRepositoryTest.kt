package com.example.repository

import com.example.createDataSource
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import javax.sql.DataSource
import kotlin.test.*
import kotlin.test.assertIs

class TaskRepositoryTest {

    private lateinit var dataSource: DataSource
    private lateinit var repository: TaskRepository

    @BeforeEach
    fun setUp() {
        dataSource = createDataSource()
        repository = TaskRepository(dataSource)
    }

    @AfterEach
    fun tearDown() {
        dataSource.connection.use { connection ->
            connection.createStatement().use {
                it.executeUpdate("DELETE FROM todo.task")
            }
        }

        (dataSource as HikariDataSource).close()
    }

    @Test
    fun `create task`() {
        val task = repository.create("Learn Ktor", false)

        assertIs<Number>(task.id)
        assertEquals("Learn Ktor", task.description)
    }
}