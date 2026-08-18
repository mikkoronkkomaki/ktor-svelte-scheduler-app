package com.example

import com.example.Routing.configureRouting
import com.example.repository.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    val dataSource = createDataSource()
    val taskRepository = TaskRepository(dataSource)

    configureRouting(taskRepository)
}