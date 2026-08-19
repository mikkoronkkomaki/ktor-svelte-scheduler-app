package com.example.model

import kotlinx.serialization.Serializable

@Serializable

data class CreateAppointmentRequest(

    val description: String,
    val done: Boolean

)

@Serializable

data class Appointment(

    val id: Int,

    val description: String,

    val done: Boolean

)