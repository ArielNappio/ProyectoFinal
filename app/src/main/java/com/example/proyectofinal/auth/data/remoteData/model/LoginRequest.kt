package com.example.proyectofinal.auth.data.remoteData.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email:String,
    val password:String
)