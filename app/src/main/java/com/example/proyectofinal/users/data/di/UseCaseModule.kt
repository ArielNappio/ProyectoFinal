package com.example.proyectofinal.users.data.di

import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
}
