package com.example.proyectofinal.userpreferences.di

import com.example.proyectofinal.userpreferences.data.manager.DataStoreManager
import com.example.proyectofinal.userpreferences.data.reporsitory.UserPreferencesRepoImpl
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val preferencesModule = module {
    single { DataStoreManager(get()) }
    single<UserPreferencesRepository> { UserPreferencesRepoImpl(get()) }
    viewModel { PreferencesViewModel(get()) }
}
