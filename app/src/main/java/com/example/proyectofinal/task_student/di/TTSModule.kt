package com.example.proyectofinal.task_student.di

import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val ttsModule = module {

    single { TextToSpeechManager(androidContext()) }

}