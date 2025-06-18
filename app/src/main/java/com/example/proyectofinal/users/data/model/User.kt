package com.example.proyectofinal.users.data.model

import kotlinx.serialization.Serializable


@Serializable
data class User(
   val id: String,
   val userName: String,
   val fullName: String,
   val email: String,
   val password: String? = null,
   val phoneNumber: String,
   val roles: List<String>
)
