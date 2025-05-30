package com.example.proyectofinal.text_editor.data.repository

import android.content.Context
import java.io.File

interface PdfRemoteRepository {
    suspend fun downloadPdf(context: Context, url: String): File?
}