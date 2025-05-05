package com.example.proyectofinal.di

import com.example.proyectofinal.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.domain.usecase.GetOrderByIdUseCase
import com.example.proyectofinal.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.domain.usecase.SavePhotoToGalleryUseCase
import com.example.proyectofinal.domain.usecase.UpdateOrderUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::SavePhotoToGalleryUseCase)

    factory { GetOrdersUseCase(get()) }
    factory { GetOrderByIdUseCase(get()) }
    factory { CreateOrderUseCase(get()) }
    factory { UpdateOrderUseCase(get()) }
    factory { DeleteOrderUseCase(get()) }

}