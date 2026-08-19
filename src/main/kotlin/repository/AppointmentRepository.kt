package com.example.repository

import com.example.model.Appointment
import javax.sql.DataSource

class AppointmentRepository(
    private val dataSource: DataSource
) {

    fun create(description: String, done: Boolean): Appointment {
        val sql = """
            INSERT INTO scheduler.appointment (description, done)
            VALUES (?, ?)
            RETURNING id, description, done
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, description)
                statement.setBoolean(2, done)
                statement.executeQuery().use { result ->
                    result.next()

                    return Appointment(
                        id = result.getInt("id"),
                        description = result.getString("description"),
                        done = result.getBoolean("done")
                    )

                }
            }
        }
    }

    fun getAll(): List<Appointment> {
        val sql = """
            SELECT id, description, done
            FROM scheduler.appointment
            """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { result ->
                    val appointments = mutableListOf<Appointment>()

                    while (result.next()) {
                        appointments.add(
                            Appointment(
                                id = result.getInt("id"),
                                description = result.getString("description"),
                                done = result.getBoolean("done")
                            )
                        )
                    }
                    return appointments
                }
            }
        }
    }

    fun getById(id: Int): Appointment? {
        val sql = """
            SELECT id, description, done
            FROM scheduler.appointment
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        Appointment(
                            id = resultSet.getInt("id"),
                            description = resultSet.getString("description"),
                            done = resultSet.getBoolean("done")
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }
}