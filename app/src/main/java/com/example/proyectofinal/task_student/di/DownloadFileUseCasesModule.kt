package com.example.proyectofinal.task_student.di

import com.example.proyectofinal.task_student.domain.DownloadAsPdfUseCase
import org.koin.dsl.module

val downloadFileUseCasesModule = module {
    single { DownloadAsPdfUseCase() }
}