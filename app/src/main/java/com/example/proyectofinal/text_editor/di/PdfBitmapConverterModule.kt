package com.example.proyectofinal.text_editor.di

import com.example.proyectofinal.text_editor.domain.PdfBitmapConverter
import org.koin.dsl.module

val pdfBitmapConverterModule = module {

    single { PdfBitmapConverter() }

}