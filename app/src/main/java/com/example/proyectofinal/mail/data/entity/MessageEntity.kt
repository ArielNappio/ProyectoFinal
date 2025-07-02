package com.example.proyectofinal.mail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey val id: Int,
    val isDraft: Boolean,
    val userFromId: String,
    val userToId: String,
    val sender: String,
    val subject: String,
    val date: String,
    val content: String,
    val filePath: String?,
    val isResponse: Boolean,
    val responseText: String?,
    val isRead: Boolean = false
)