package com.example.proyectofinal.audio.data.repository

import com.example.proyectofinal.audio.data.local.AudioDao
import com.example.proyectofinal.audio.data.model.RecordedAudioEntity
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.domain.model.toDomain
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import java.io.File

class AudioRepositoryImpl(
    private val audioDao: AudioDao
) : AudioRepository{

    override suspend fun saveAudio(path: String, taskId: String?, title: String, page: Int, date: String) {
        val entity = RecordedAudioEntity(
            filePath = path,
            timestamp = System.currentTimeMillis(),
            associatedTaskId = taskId,
            title = title,
            page = page,
            date = date
        )
        audioDao.insertAudio(entity)
    }

    override suspend fun getAudiosForTask(taskId: String): List<RecordedAudio> {
        return audioDao.getAudiosByTask(taskId).map { it.toDomain() }
    }

    override suspend fun getAllAudios(): List<RecordedAudio> {
        return audioDao.getAllAudios().map { it.toDomain() }
    }

    override suspend fun deleteAudio(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        audioDao.deleteByFilePath(filePath)
    }
}