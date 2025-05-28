package com.example.proyectofinal.librarian.di

import com.example.proyectofinal.librarian.viewmodel.createOrderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appTaskModule = module {
    viewModel { createOrderViewModel(
        getOrdersUseCase = TODO(),
        createOrderUseCase = TODO()
    ) }

}
