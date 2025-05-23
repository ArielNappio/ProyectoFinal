package com.example.proyectofinal.student.domain.model

data class AudioComment(
    val id: Int,
    val title: String,
    val uri: String,
    val page: Int = 0,
    val date: String = ""
)
