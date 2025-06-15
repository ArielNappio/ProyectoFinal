package com.example.proyectofinal.users.data.di

import UpdateUserUseCase
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.users.data.provider.UserProviderImpl
import com.example.proyectofinal.users.domain.provider.UserProvider
import com.example.proyectofinal.users.domain.provider.usecase.CreateUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import io.ktor.client.HttpClient
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userModule = module {

    single  { UserProviderImpl(get()) } // El get() aquí obtiene el HttpClient

        viewModel {
        UserViewModel(
            getUserUserCase = get(),
            deleteUserUseCase = get(),
            updateUserUseCase = get() ,
            createUserUseCase = get()
        )
    }


    factory { GetUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { CreateUserUseCase(get()) }

}
