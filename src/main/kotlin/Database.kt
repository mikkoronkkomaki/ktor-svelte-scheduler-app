package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun createDataSource(): DataSource {
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://0.0.0.0:5432/scheduler"
        username = "scheduler"
        password = "scheduler"
        maximumPoolSize = 5
    }

    return HikariDataSource(config)
}