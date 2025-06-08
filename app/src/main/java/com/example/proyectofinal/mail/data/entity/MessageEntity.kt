package com.example.proyectofinal.mail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isDraft: Boolean,
    val userFromId: String,
    val userToId: String,
    val sender: String,
    val subject: String,
    val date: Date,
    val content: String,
    val filePath: String?,
    val isResponse: Boolean,
    val responseText: String?
)