package com.example.proyectofinal.users.data.di

import com.example.proyectofinal.users.domain.usecase.UpdateUserUseCase
import com.example.proyectofinal.users.data.provider.UserProviderImpl
import com.example.proyectofinal.users.domain.provider.UserProvider
import com.example.proyectofinal.users.domain.usecase.CreateUserUseCase
import com.example.proyectofinal.users.domain.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.usecase.GetUserUseCase
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userModule = module {

    single<UserProvider> { UserProviderImpl(get()) }

    viewModel {
        UserViewModel(
            getUserUserCase = get(),
            deleteUserUseCase = get(),
            updateUserUseCase = get(),
            createUserUseCase = get()
        )
    }

    factory { GetUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { CreateUserUseCase(get()) }

}
