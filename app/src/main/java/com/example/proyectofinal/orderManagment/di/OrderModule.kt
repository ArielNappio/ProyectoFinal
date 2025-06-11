package com.example.proyectofinal.orderManagment.di

import com.example.proyectofinal.orderManagment.data.provider.OrderManagmentImpl
import com.example.proyectofinal.orderManagment.domain.provider.OrderManagmentProvider
import com.example.proyectofinal.orderManagment.domain.usecase.GetOrdersManagmentUseCase
import org.koin.dsl.module

val orderModule = module {
    single<OrderManagmentProvider> { OrderManagmentImpl(get(), get()) }
    factory { GetOrdersManagmentUseCase(get()) }
}