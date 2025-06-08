package com.example.proyectofinal.mail.domain.model

import java.util.Date

data class MessageModel(
    val id: Int,
    val userFromId: String,
    val studentId: String,
    val isDraft: Boolean,
    val sender: String,
    val subject: String,
    val date: Date,
    val content: String,
    val formPath: String? = null,
    val attachments: List<String> = emptyList(),
    val isResponse: Boolean,
    val responseText: String? = null
)
