package com.example.proyectofinal.users.data.model

data class User(
    val userName: String,
    val email: String,
    val fullName: String,
    val roles: List<String>
)
