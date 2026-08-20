package com.example.repository

import com.example.model.Specialist
import java.sql.ResultSet
import javax.sql.DataSource

class SpecialistRepository(
    private val dataSource: DataSource
) {

    fun create(firstName: String, lastName: String, profession: String): Specialist {
        val sql = """
            INSERT INTO scheduler.specialist (first_name, last_name, profession)
            VALUES (?, ?, ?)
            RETURNING id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, firstName)
                statement.setString(2, lastName)
                statement.setString(3, profession)
                statement.executeQuery().use { result ->
                    result.next()
                    return getById(result.getInt("id"))
                        ?: error("Failed to load inserted specialist")
                }
            }
        }
    }

    fun getAll(): List<Specialist> {
        val sql = """
            SELECT id, first_name, last_name, profession
            FROM scheduler.specialist
            ORDER BY id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { result ->
                    val specialists = mutableListOf<Specialist>()
                    while (result.next()) {
                        specialists.add(result.toSpecialist())
                    }
                    return specialists
                }
            }
        }
    }

    fun getById(id: Int): Specialist? {
        val sql = """
            SELECT id, first_name, last_name, profession
            FROM scheduler.specialist
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { result ->
                    return if (result.next()) result.toSpecialist() else null
                }
            }
        }
    }

    fun update(id: Int, firstName: String, lastName: String, profession: String): Specialist? {
        val sql = """
            UPDATE scheduler.specialist
            SET first_name = ?, last_name = ?, profession = ?
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, firstName)
                statement.setString(2, lastName)
                statement.setString(3, profession)
                statement.setInt(4, id)
                val updated = statement.executeUpdate()
                return if (updated > 0) getById(id) else null
            }
        }
    }

    fun delete(id: Int): Boolean {
        val sql = "DELETE FROM scheduler.specialist WHERE id = ?"

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                return statement.executeUpdate() > 0
            }
        }
    }

    private fun ResultSet.toSpecialist() = Specialist(
        id = getInt("id"),
        firstName = getString("first_name"),
        lastName = getString("last_name"),
        profession = getString("profession")
    )
}

