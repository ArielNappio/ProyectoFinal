package com.example.proyectofinal.users.data.model


data class User(
   val id: String,
   val userName: String,
   val fullName: String,
   val email: String,
   val password :String,
   val phoneNumber : String,
   val dni: String,
   val roles: List<String>,
)
