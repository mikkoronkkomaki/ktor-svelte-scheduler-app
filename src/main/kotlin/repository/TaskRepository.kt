package com.example.repository

import com.example.model.Task
import javax.sql.DataSource

class TaskRepository(
    private val dataSource: DataSource
) {

    fun create(description: String, done: Boolean): Task {
        val sql = """
            INSERT INTO task (description, done)
            VALUES (?, ?)
            RETURNING id, description, done
        """.trimIndent()

        print("---->>> KERTTULII")
        
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
}