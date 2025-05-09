package com.example.proyectofinal.auth.data.remoteData.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val userName: String,
    val email: String,
    val fullName: String,
    val roles: List<String>
)