package com.example.proyectofinal.librarian.di

import com.example.proyectofinal.librarian.viewmodel.CreateOrderViewModel
import com.example.proyectofinal.order.data.repository.OrderRepoImpl
import com.example.proyectofinal.order.data.repository.OrderRepository
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appTaskModule = module {
    viewModel {
        CreateOrderViewModel(
            getOrdersUseCase = get(),
            createOrderUseCase = get()
        )
    }

    single { HttpClient() }
    single<OrderRepository> { OrderRepoImpl(get()) }
}



