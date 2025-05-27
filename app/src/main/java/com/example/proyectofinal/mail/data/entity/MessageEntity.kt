package com.example.proyectofinal.mail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false,
    val userFromId: String, //user que envia el mensaje
    val userToID: String, //user que recibe el mensaje
    val sender: String,
    val subject: String,
    val date: String,
    val content: String
)
