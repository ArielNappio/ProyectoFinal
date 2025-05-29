package com.example.proyectofinal.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email:String,
    val password:String
)