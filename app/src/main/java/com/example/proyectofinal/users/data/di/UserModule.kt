package com.example.proyectofinal.users.data.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.dsl.module

val userModule = module {

    factory { UserViewModel(
        get(),
        get()
    ) }
}