package com.example.proyectofinal.data.remoteData.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val date: String,
    val temperatureC: Int,
    val temperatureF: Int,
    val summary: String
)