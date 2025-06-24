package com.example.proyectofinal.audio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectofinal.audio.data.model.RecordedAudioEntity

@Database(entities = [RecordedAudioEntity::class], version = 3)
abstract class AudioDatabase : RoomDatabase(){
    abstract fun audioDao(): AudioDao
}