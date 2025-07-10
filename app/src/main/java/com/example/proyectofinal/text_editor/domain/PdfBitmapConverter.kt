package com.example.proyectofinal.text_editor.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File

class PdfBitmapConverter() {
    private var renderer: PdfRenderer? = null

    suspend fun pdfToBitmaps(pdfFile: File): List<Bitmap> {
        return withContext(Dispatchers.IO) {
            renderer?.close()

            ParcelFileDescriptor
                .open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                ?.use { descriptor ->
                    with(PdfRenderer(descriptor)) {
                        renderer = this

                        return@withContext (0 until pageCount).map { index ->
                            async {
                                openPage(index).use { page ->
                                    val scaleFactor = 4
                                    val bitmap = createBitmap(page.width * scaleFactor, page.height * scaleFactor)

                                    Canvas(bitmap).apply {
                                        drawColor(Color.WHITE)
                                        drawBitmap(bitmap, 0f, 0f, null)
                                    }

                                    page.render(
                                        bitmap,
                                        null,
                                        null,
                                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                    )

                                    bitmap
                                }
                            }
                        }.awaitAll()
                    }
                }
            return@withContext emptyList()
        }
    }
}