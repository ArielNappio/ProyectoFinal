package com.example.proyectofinal.student.data.di

import com.example.proyectofinal.student.data.repository.CommentsRepository
import org.koin.dsl.module

val CommentsRepositoryModule = module {
    single { CommentsRepository() }
}