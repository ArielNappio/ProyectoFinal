package com.example.proyectofinal.auth.di

import com.example.proyectofinal.auth.data.provider.AuthRemoteProviderImpl
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRemoteProvider> {
        AuthRemoteProviderImpl(get(),get())
        AuthRemoteProviderImpl(get(),get())
    }
}