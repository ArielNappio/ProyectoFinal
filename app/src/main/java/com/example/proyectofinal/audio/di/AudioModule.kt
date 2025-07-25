package com.example.proyectofinal.audio.di

import android.content.Context
import androidx.room.Room
import com.example.proyectofinal.audio.data.local.AudioDatabase
import com.example.proyectofinal.audio.data.repository.AudioRepositoryImpl
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.audio.recorder.AudioRecorderManager
import com.example.proyectofinal.audio.speechrecognizer.SpeechRecognizerManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        // This is how you provide your Room database instance
        Room.databaseBuilder(
            androidContext(), // Koin's way to get application context
            AudioDatabase::class.java,
            "audio_database" // Your database name
        )
            .fallbackToDestructiveMigration()
            .build()
    }

}

val audioModule = module {
    single<AudioRepositoryImpl> { AudioRepositoryImpl(get()) }
    single<AudioRepository> { get<AudioRepositoryImpl>() }
    single { get<AudioDatabase>().audioDao() }
    single { AudioRecorderManager(androidContext()) }
    single { AudioPlayerManager() }

    factory { (context: Context, onResult: (String) -> Unit, onError: ((String) -> Unit)?) ->
        SpeechRecognizerManager(
            context = context,
            onResult = onResult,
            onError = onError
        )
    }

}