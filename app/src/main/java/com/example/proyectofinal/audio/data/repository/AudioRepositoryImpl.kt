package com.example.proyectofinal.audio.data.repository

import android.media.MediaPlayer
import android.util.Log
import com.example.proyectofinal.audio.data.local.AudioDao
import com.example.proyectofinal.audio.data.model.RecordedAudioEntity
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.util.toDomain
import java.io.File

class AudioRepositoryImpl(
    private val audioDao: AudioDao
) : AudioRepository{

    override suspend fun saveAudio(path: String, taskId: String?, title: String, page: Int, date: String) {

        val duration = getAudioDuration(path)

        Log.d("Audio duration", "$duration despues de calcular")
        val entity = RecordedAudioEntity(
            filePath = path,
            timestamp = System.currentTimeMillis(),
            associatedTaskId = taskId,
            title = title,
            page = page,
            date = date,
            duration = duration
        )
        Log.d("Audio duration", "${entity.duration} duration in entity b4 save")

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

    fun getAudioDuration(path: String): Long {
        return try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            val duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()
            Log.d("Audio duration", "$duration")
            duration
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}