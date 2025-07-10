package com.example.proyectofinal.auth.di

import com.example.proyectofinal.auth.data.provider.AuthRemoteProviderImpl
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.auth.domain.usecases.GetMeUseCase
import com.example.proyectofinal.auth.domain.usecases.PostLoginUseCase
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRemoteProvider> {
        AuthRemoteProviderImpl(get(),get())
        AuthRemoteProviderImpl(get(),get())
    }

    factory { PostLoginUseCase(get()) }
    factory { GetMeUseCase(get()) }
}