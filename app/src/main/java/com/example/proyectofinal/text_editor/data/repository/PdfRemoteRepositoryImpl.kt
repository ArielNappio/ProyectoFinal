package com.example.proyectofinal.text_editor.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class PdfRemoteRepositoryImpl(): PdfRemoteRepository {

    override suspend fun downloadPdf(context: Context, url: String): File? = withContext(Dispatchers.IO) {
        // Temp file in cache
        val file = File(context.cacheDir, "temp.pdf")

        return@withContext try {
            val inputStream = URL(url).openStream() // TODO replace with actual url and http client
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error downloading PDF from $url")
            null
        }
    }



}