package com.example.proyectofinal.data.remoteData.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val expiration: String,
    val userId: String
)
