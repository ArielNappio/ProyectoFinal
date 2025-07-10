package com.example.proyectofinal.audio.domain.model

data class RecordedAudio(
    val id: Int,
    val title: String = "",
    val page: Int = 0,
    val filePath: String,
    val timestamp: Long,
    val date: String,
    val associatedTaskId: String? = null, // si lo vinculás a tareas
    val duration: Long
)
