package com.example.proyectofinal.users.data.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.users.data.provider.UserProviderImpl
import com.example.proyectofinal.users.domain.provider.UserProvider
import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userModule = module {
    // Definimos HttpClient como singleton para que se reutilice
    single { HttpClient() }

    viewModel {
        UserViewModel(
            getUserUserCase = get(),
            deleteUserUseCase = get()
        )
    }

    single<UserProvider> { UserProviderImpl(get()) } // El get() aquí obtiene el HttpClient

    factory { GetUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
}
