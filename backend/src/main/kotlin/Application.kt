package com.example

import com.example.routing.configureAppointmentRouting
import com.example.routing.configureClientRouting
import com.example.routing.configureSpecialistRouting
import com.example.repository.AppointmentRepository
import com.example.repository.ClientRepository
import com.example.repository.SpecialistRepository
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.cors.routing.CORS

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()

    install(CORS) {
        allowHost("localhost:5173")
        allowHost("127.0.0.1:5173")
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader("Content-Type")
        allowHeader("Accept")
        allowCredentials = true
    }

    val dataSource = createDataSource()
    val appointmentRepository = AppointmentRepository(dataSource)
    val clientRepository = ClientRepository(dataSource)
    val specialistRepository = SpecialistRepository(dataSource)

    configureAppointmentRouting(appointmentRepository)
    configureClientRouting(clientRepository)
    configureSpecialistRouting(specialistRepository)
}