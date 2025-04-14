package com.example.proyectofinal.di

import com.example.proyectofinal.data.remoteData.repository.RemoteRepoImpl
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<RemoteRepository> { RemoteRepoImpl(get()) }
}