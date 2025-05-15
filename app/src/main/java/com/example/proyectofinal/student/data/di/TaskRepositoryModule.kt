package com.example.proyectofinal.student.data.di

import com.example.proyectofinal.student.data.repository.TaskRepository
import org.koin.dsl.module

val taskRepositoryModule = module {
    single { TaskRepository() }
}