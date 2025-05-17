package com.example.proyectofinal.core.di

import com.example.proyectofinal.camera.presentation.viewmodel.CameraViewModel
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CameraViewModel)
    viewModel{ ThemeViewModel() }
    viewModel{ LoginViewModel(get(), get()) }
    viewModel{ MainViewModel(get(), get()) }
    viewModel{ HomeScreenViewModel() }
}