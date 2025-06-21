package com.example.proyectofinal.userpreferences.domain.model

data class UserPreferences(
    val fontSize: Float,
    val fontFamily: String,
    val profileImageUri: String? = null
)
