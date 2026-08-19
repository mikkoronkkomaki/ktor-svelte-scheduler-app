package com.example

import com.example.routing.configureAppointmentRouting
import com.example.repository.AppointmentRepository
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    val dataSource = createDataSource()
    val appointmentRepository = AppointmentRepository(dataSource)

    configureAppointmentRouting(appointmentRepository)
}