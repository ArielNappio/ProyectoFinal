package com.example.proyectofinal.audio.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectofinal.audio.data.model.RecordedAudioEntity

@Dao
interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: RecordedAudioEntity)

    @Query("SELECT * FROM recorded_audio WHERE associatedTaskId = :taskId")
    suspend fun getAudiosByTask(taskId: String): List<RecordedAudioEntity>

    @Query("SELECT * FROM recorded_audio")
    suspend fun getAllAudios(): List<RecordedAudioEntity>

    @Query("DELETE FROM recorded_audio WHERE filePath = :filePath")
    suspend fun deleteByFilePath(filePath: String)
}