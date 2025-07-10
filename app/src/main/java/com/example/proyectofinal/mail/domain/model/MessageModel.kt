package com.example.proyectofinal.mail.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val id: Int,
    val userFromId: String,
    val userToId: String,
    val isDraft: Boolean,
    val sender: String,
    val subject: String,
    val date: String,
    val content: String,
    val file: String? = null,
    val isResponse: Boolean = false,
    val responseText: String? = null
)

@Serializable
data class MessageModelDto(
    val id: Int,
    val userFromId: String,
    val userToId: String,
    val isDraft: Boolean,
    val sender: String,
    val subject: String,
    val date: String,
    val content: String,
    val file: String? = null,
    val isResponse: Boolean = false,
    val responseText: String? = null,
    val isRead: Boolean?
)
