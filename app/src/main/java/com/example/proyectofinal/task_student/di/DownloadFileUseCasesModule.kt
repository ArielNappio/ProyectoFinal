package com.example.proyectofinal.task_student.di

import com.example.proyectofinal.task_student.domain.usecase.DownloadAsMp3UseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsPdfUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsTxtUseCase
import org.koin.dsl.module

val downloadFileUseCasesModule = module {
    single { DownloadAsPdfUseCase() }
    single { DownloadAsMp3UseCase() }
    single { DownloadAsTxtUseCase() }
}