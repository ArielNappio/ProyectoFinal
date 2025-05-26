package com.example.proyectofinal.mail.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recorded_audio")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, //user que envia el mensaje
    val sender: String,
    val subject: String,
    val date: String,
    val content: String
)
