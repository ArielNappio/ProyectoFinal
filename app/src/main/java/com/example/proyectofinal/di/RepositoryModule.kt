package com.example.proyectofinal.di

import com.example.proyectofinal.data.remoteData.repository.RemoteRepoImpl
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import com.example.proyectofinal.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.domain.usecase.GetOrderByIdUseCase
import com.example.proyectofinal.domain.usecase.UpdateOrderUseCase
import org.koin.dsl.module

val repositoryModule = module {
    single<RemoteRepository> { RemoteRepoImpl(get()) }

    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    factory { CreateOrderUseCase(get()) }
    factory { UpdateOrderUseCase(get()) }
    factory { DeleteOrderUseCase(get()) }
}