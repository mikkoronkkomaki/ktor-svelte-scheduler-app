package com.example.repository

import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.Client
import com.example.model.Specialist
import java.sql.ResultSet
import javax.sql.DataSource

class AppointmentRepository(
    private val dataSource: DataSource
) {

    fun create(
        description: String,
        startTime: String,
        endTime: String,
        status: AppointmentStatus,
        clientId: Int?,
        specialistId: Int?
    ): Appointment {
        val sql = """
            INSERT INTO scheduler.appointment (
                description,
                start_time,
                end_time,
                status,
                client_id,
                specialist_id
            )
            VALUES (?, CAST(? AS timestamp), CAST(? AS timestamp), CAST(? AS scheduler.appointment_status), ?, ?)
            RETURNING id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, description)
                statement.setString(2, startTime)
                statement.setString(3, endTime)
                statement.setString(4, status.toDbValue())
                statement.setObject(5, clientId)
                statement.setObject(6, specialistId)
                statement.executeQuery().use { result ->
                    result.next()
                    return getById(result.getInt("id"))
                        ?: error("Failed to load inserted appointment")
                }
            }
        }
    }

    fun getAll(): List<Appointment> {
        val sql = """
            SELECT
                a.id,
                a.description,
                a.start_time,
                a.end_time,
                a.status,
                a.client_id,
                a.specialist_id,
                c.id AS c_id,
                c.first_name AS c_first_name,
                c.last_name AS c_last_name,
                s.id AS s_id,
                s.first_name AS s_first_name,
                s.last_name AS s_last_name,
                s.profession AS s_profession
            FROM scheduler.appointment a
            LEFT JOIN scheduler.client c ON c.id = a.client_id
            LEFT JOIN scheduler.specialist s ON s.id = a.specialist_id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { result ->
                    val appointments = mutableListOf<Appointment>()

                    while (result.next()) {
                        appointments.add(result.toAppointment())
                    }
                    return appointments
                }
            }
        }
    }

    fun getById(id: Int): Appointment? {
        val sql = """
            SELECT
                a.id,
                a.description,
                a.start_time,
                a.end_time,
                a.status,
                a.client_id,
                a.specialist_id,
                c.id AS c_id,
                c.first_name AS c_first_name,
                c.last_name AS c_last_name,
                s.id AS s_id,
                s.first_name AS s_first_name,
                s.last_name AS s_last_name,
                s.profession AS s_profession
            FROM scheduler.appointment a
            LEFT JOIN scheduler.client c ON c.id = a.client_id
            LEFT JOIN scheduler.specialist s ON s.id = a.specialist_id
            WHERE a.id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { resultSet ->
                    return if (resultSet.next()) {
                        resultSet.toAppointment()
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun update(
        id: Int,
        description: String,
        startTime: String,
        endTime: String,
        status: AppointmentStatus,
        clientId: Int?,
        specialistId: Int?
    ): Appointment? {
        val sql = """
            UPDATE scheduler.appointment
            SET description = ?,
                start_time = CAST(? AS timestamp),
                end_time = CAST(? AS timestamp),
                status = CAST(? AS scheduler.appointment_status),
                client_id = ?,
                specialist_id = ?
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, description)
                statement.setString(2, startTime)
                statement.setString(3, endTime)
                statement.setString(4, status.toDbValue())
                statement.setObject(5, clientId)
                statement.setObject(6, specialistId)
                statement.setInt(7, id)
                val updated = statement.executeUpdate()
                return if (updated > 0) getById(id) else null
            }
        }
    }

    fun delete(id: Int): Boolean {
        val sql = "DELETE FROM scheduler.appointment WHERE id = ?"

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                return statement.executeUpdate() > 0
            }
        }
    }

    private fun ResultSet.toAppointment(): Appointment {
        val clientRefId = getNullableInt("client_id")
        val specialistRefId = getNullableInt("specialist_id")

        return Appointment(
            id = getInt("id"),
            description = getString("description"),
            startTime = getString("start_time"),
            endTime = getString("end_time"),
            status = mapStatus(getString("status")),
            clientId = clientRefId,
            specialistId = specialistRefId,
            client = if (getNullableInt("c_id") != null) {
                Client(
                    id = getInt("c_id"),
                    firstName = getString("c_first_name"),
                    lastName = getString("c_last_name")
                )
            } else {
                null
            },
            specialist = if (getNullableInt("s_id") != null) {
                Specialist(
                    id = getInt("s_id"),
                    firstName = getString("s_first_name"),
                    lastName = getString("s_last_name"),
                    profession = getString("s_profession")
                )
            } else {
                null
            }
        )
    }

    private fun ResultSet.getNullableInt(column: String): Int? {
        val value = getInt(column)
        return if (wasNull()) null else value
    }

    private fun AppointmentStatus.toDbValue(): String = when (this) {
        AppointmentStatus.RESERVED -> "reserved"
        AppointmentStatus.CANCELLED -> "cancelled"
        AppointmentStatus.DONE -> "done"
        AppointmentStatus.NO_SHOW -> "no-show"
    }

    private fun mapStatus(value: String): AppointmentStatus = when (value) {
        "reserved" -> AppointmentStatus.RESERVED
        "cancelled" -> AppointmentStatus.CANCELLED
        "done" -> AppointmentStatus.DONE
        "no-show" -> AppointmentStatus.NO_SHOW
        else -> error("Unknown appointment status: $value")
    }
}