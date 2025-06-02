package com.example.proyectofinal.student.data.di

import com.example.proyectofinal.student.data.provider.ProcessedDocumentProviderImpl
import com.example.proyectofinal.student.domain.provider.ProcessedDocumentProvider
import org.koin.dsl.module

val taskProviderModule = module {
    single<ProcessedDocumentProvider> { ProcessedDocumentProviderImpl(get()) }
}