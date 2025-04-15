package com.example.proyectofinal.di

import com.example.proyectofinal.data.usecases.SavePhotoToGalleryUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::SavePhotoToGalleryUseCase)
}