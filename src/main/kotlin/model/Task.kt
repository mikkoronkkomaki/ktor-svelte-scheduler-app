package com.example.model

import kotlinx.serialization.Serializable

@Serializable

data class CreateTaskRequest(

    val description: String,
    val done: Boolean

)

@Serializable

data class Task(

    val id: Int,

    val description: String,

    val done: Boolean

)