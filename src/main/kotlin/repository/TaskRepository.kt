package com.example.repository

import com.example.model.Task
import sun.font.GlyphLayout.done
import javax.sql.DataSource

class TaskRepository(
    private val dataSource: DataSource
) {

    fun create(description: String, done: Boolean): Task {
        val sql = """
            INSERT INTO todo.task (description, done)
            VALUES (?, ?)
            RETURNING id, description, done
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, description)
                statement.setBoolean(2, done)
                statement.executeQuery().use { result ->
                    result.next()

                    return Task(
                        id = result.getInt("id"),
                        description = result.getString("description"),
                        done = result.getBoolean("done")
                    )

                }
            }
        }
    }

    fun getAll(): List<Task> {
        val sql = """
            SELECT id, description, done
            FROM todo.task
            """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { result ->
                    val tasks = mutableListOf<Task>()

                    while (result.next()) {
                        tasks.add(
                            Task(
                                id = result.getInt("id"),
                                description = result.getString("description"),
                                done = result.getBoolean("done")
                            )
                        )
                    }
                    return tasks
                }
            }
        }
    }

    fun getById(id: Int): Task? {
        val sql = """
            SELECT id, description, done
            FROM todo.task
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        println("löyty")
                        Task(
                            id = resultSet.getInt("id"),
                            description = resultSet.getString("description"),
                            done = resultSet.getBoolean("done")
                        )
                    } else {
                        println("pallit")
                        null
                    }
                }
            }
        }
    }
}