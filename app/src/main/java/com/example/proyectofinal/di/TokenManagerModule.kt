package com.example.proyectofinal.di

import com.example.proyectofinal.data.local.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tokenManagerModule = module {
    single { TokenManager(androidContext()) }
}