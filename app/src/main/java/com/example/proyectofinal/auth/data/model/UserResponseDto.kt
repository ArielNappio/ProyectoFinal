package com.example.proyectofinal.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val userName: String,
    val email: String,
    val fullName: String,
    val roles: List<String>
)