package com.example.proyectofinal.audio.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recorded_audio")
data class RecordedAudioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val page: Int = 0,
    val filePath: String,
    val timestamp: Long,
    val date: String,
    val associatedTaskId: String? = null // si lo vinculás a tareas
)
