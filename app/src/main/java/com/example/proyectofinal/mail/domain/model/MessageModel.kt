package com.example.proyectofinal.mail.domain.model

data class MessageModel(
    val id: Int,
//    val userFromId: String,
//    val userToID: String,
//    val isDraft: Boolean,
    val sender: String,
    val subject: String,
    val date: String,
    val content: String,
    val filePath: String? = null
)
