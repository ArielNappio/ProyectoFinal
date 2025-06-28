package com.example.proyectofinal.core.di

import com.example.proyectofinal.order.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.order.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrderByIdUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.camera.domain.usecases.SavePhotoToGalleryUseCase
import com.example.proyectofinal.order.domain.usecase.UpdateOrderUseCase
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
