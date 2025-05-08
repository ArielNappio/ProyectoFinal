package com.example.proyectofinal.auth.di

import com.example.proyectofinal.auth.data.local.TokenManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tokenManagerModule = module {
    single { TokenManager(androidContext()) }
}