package com.example

import com.example.routing.configureAppointmentRouting
import com.example.routing.configureClientRouting
import com.example.routing.configureSpecialistRouting
import com.example.repository.AppointmentRepository
import com.example.repository.ClientRepository
import com.example.repository.SpecialistRepository
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    val dataSource = createDataSource()
    val appointmentRepository = AppointmentRepository(dataSource)
    val clientRepository = ClientRepository(dataSource)
    val specialistRepository = SpecialistRepository(dataSource)

    configureAppointmentRouting(appointmentRepository)
    configureClientRouting(clientRepository)
    configureSpecialistRouting(specialistRepository)
}