package com.example.proyectofinal.audio.domain.repository

import com.example.proyectofinal.audio.domain.model.RecordedAudio

interface AudioRepository {
    suspend fun saveAudio(path: String, taskId: String?, title: String, page: Int, date: String)
    suspend fun getAudiosForTask(taskId: String): List<RecordedAudio>
    suspend fun getAllAudios(): List<RecordedAudio>
    suspend fun deleteAudio(path: String)
}