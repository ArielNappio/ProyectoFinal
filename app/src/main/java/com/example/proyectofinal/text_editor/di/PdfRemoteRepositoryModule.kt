package com.example.proyectofinal.text_editor.di

import com.example.proyectofinal.text_editor.data.repository.PdfProviderImpl
import org.koin.dsl.module

val pdfRemoteRepositoryModule = module {

    single { PdfProviderImpl() }

}