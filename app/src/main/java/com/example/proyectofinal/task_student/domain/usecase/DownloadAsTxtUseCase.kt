package com.example.proyectofinal.task_student.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

class DownloadAsTxtUseCase {

    operator fun invoke(context: Context, documentTitle: String, documentContent: String) {
        val contentResolver = context.contentResolver
        val downloadsUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            Uri.EMPTY // Handle pre-Q versions if needed
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$documentTitle.txt")
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = contentResolver.insert(downloadsUri, contentValues)

        try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(documentContent.toByteArray())
                }
            } ?: throw Exception("Failed to create file URI")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}