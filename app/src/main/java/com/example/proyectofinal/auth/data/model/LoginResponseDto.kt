package com.example.proyectofinal.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val expiration: String,
    val userId: String
)