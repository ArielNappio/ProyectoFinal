package com.example.proyectofinal.mail.domain.model

data class MessageModel(
    val sender: String,
    val subject: String,
    val date: String,
    val content: String
)
