package com.example.proyectofinal.users.data.di

import com.example.proyectofinal.users.data.provider.UserProviderImpl
import com.example.proyectofinal.users.domain.provider.UserProvider
import org.koin.dsl.module


val repositoryModule = module {
    single<UserProvider> { UserProviderImpl(get()) }
}
