package com.example.proyectofinal.di

import com.example.proyectofinal.presentation.viewmodel.TestViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TestViewModel)
}