package com.example.proyectofinal.data.remoteData.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val fullName: String,
    val email: String,
)
