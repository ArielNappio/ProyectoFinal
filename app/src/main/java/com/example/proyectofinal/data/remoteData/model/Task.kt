package com.example.proyectofinal.data.remoteData.model

import kotlinx.serialization.Serializable


@Serializable
data class Task(
    val id: Int,
    val title: String ,
    val description : String ,
    val taskStatus: TaskStatus
)

