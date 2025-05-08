package com.example.proyectofinal.auth.di

import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepoImpl
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRemoteRepository> {
        AuthRemoteRepoImpl(get())
    }
}