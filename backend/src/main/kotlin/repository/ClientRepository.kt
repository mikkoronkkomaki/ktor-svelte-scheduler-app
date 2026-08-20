package com.example.repository

import com.example.model.Client
import java.sql.ResultSet
import javax.sql.DataSource

class ClientRepository(
    private val dataSource: DataSource
) {

    fun create(firstName: String, lastName: String): Client {
        val sql = """
            INSERT INTO scheduler.client (first_name, last_name)
            VALUES (?, ?)
            RETURNING id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, firstName)
                statement.setString(2, lastName)
                statement.executeQuery().use { result ->
                    result.next()
                    return getById(result.getInt("id"))
                        ?: error("Failed to load inserted client")
                }
            }
        }
    }

    fun getAll(): List<Client> {
        val sql = """
            SELECT id, first_name, last_name
            FROM scheduler.client
            ORDER BY id
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().use { result ->
                    val clients = mutableListOf<Client>()
                    while (result.next()) {
                        clients.add(result.toClient())
                    }
                    return clients
                }
            }
        }
    }

    fun getById(id: Int): Client? {
        val sql = """
            SELECT id, first_name, last_name
            FROM scheduler.client
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                statement.executeQuery().use { result ->
                    return if (result.next()) result.toClient() else null
                }
            }
        }
    }

    fun update(id: Int, firstName: String, lastName: String): Client? {
        val sql = """
            UPDATE scheduler.client
            SET first_name = ?, last_name = ?
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, firstName)
                statement.setString(2, lastName)
                statement.setInt(3, id)
                val updated = statement.executeUpdate()
                return if (updated > 0) getById(id) else null
            }
        }
    }

    fun delete(id: Int): Boolean {
        val sql = "DELETE FROM scheduler.client WHERE id = ?"

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                statement.setInt(1, id)
                return statement.executeUpdate() > 0
            }
        }
    }

    private fun ResultSet.toClient() = Client(
        id = getInt("id"),
        firstName = getString("first_name"),
        lastName = getString("last_name")
    )
}

