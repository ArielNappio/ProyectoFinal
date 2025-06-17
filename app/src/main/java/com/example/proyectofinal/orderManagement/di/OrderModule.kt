package com.example.proyectofinal.orderManagement.di

import androidx.room.Room
import com.example.proyectofinal.orderManagement.data.local.OrderDatabase
import com.example.proyectofinal.orderManagement.data.provider.OrderManagementImpl
import com.example.proyectofinal.orderManagement.data.repository.OrderRepositoryImpl
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import org.koin.dsl.module

val orderDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), OrderDatabase::class.java, "orders-db").build()
    }
    single { get<OrderDatabase>().orderDao() }
}


val orderModule = module {
    single<OrderManagementProvider> { OrderManagementImpl(get(), get()) }
    factory { GetTaskGroupByStudentUseCase(get()) }
    single <OrderRepository> { OrderRepositoryImpl(get(), get()) }
}