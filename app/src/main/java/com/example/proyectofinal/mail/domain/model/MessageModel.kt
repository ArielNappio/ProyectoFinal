package com.example.proyectofinal.mail.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    @Transient val id: Int = 0,
    val userFromId: String,
    val studentId: String,
    val isDraft: Boolean,
    val sender: String,
    val subject: String,
    val date: String,
    val content: String,
    val formPath: String? = null,
    val attachments: List<String> = emptyList(),
    val isResponse: Boolean = false,
    val responseText: String? = null
)
