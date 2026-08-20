package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAppointmentRequest(
    val description: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
    val clientId: Int? = null,
    val specialistId: Int? = null
)

@Serializable
data class UpdateAppointmentRequest(
    val description: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
    val clientId: Int? = null,
    val specialistId: Int? = null
)

@Serializable
data class Appointment(
    val id: Int,
    val description: String,
    val startTime: String,
    val endTime: String,
    val status: AppointmentStatus,
    val clientId: Int? = null,
    val specialistId: Int? = null,
    val client: Client? = null,
    val specialist: Specialist? = null
)

@Serializable
enum class AppointmentStatus {
    @SerialName("reserved") RESERVED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("done") DONE,
    @SerialName("no-show") NO_SHOW
}

@Serializable
data class Client(
    val id: Int,
    val firstName: String,
    val lastName: String
)

@Serializable
data class CreateClientRequest(
    val firstName: String,
    val lastName: String
)

@Serializable
data class UpdateClientRequest(
    val firstName: String,
    val lastName: String
)

@Serializable
data class Specialist(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val profession: String
)

@Serializable
data class CreateSpecialistRequest(
    val firstName: String,
    val lastName: String,
    val profession: String
)

@Serializable
data class UpdateSpecialistRequest(
    val firstName: String,
    val lastName: String,
    val profession: String
)
