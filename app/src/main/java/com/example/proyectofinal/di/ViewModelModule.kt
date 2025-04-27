package com.example.proyectofinal.di

import com.example.proyectofinal.presentation.viewmodel.TestViewModel
import com.example.proyectofinal.presentation.view.features.CameraViewModel
import com.example.proyectofinal.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.presentation.viewmodel.MainViewModel
import org.koin.core.module.dsl.viewModel

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TestViewModel)
    viewModelOf(::CameraViewModel)
    viewModel{ LoginViewModel(get(), get()) }
    viewModel{ MainViewModel(get(), get()) }
}