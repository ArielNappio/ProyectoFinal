package com.example.proyectofinal.task_student.domain.usecase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.scale
import com.example.proyectofinal.R


class DownloadAsPdfUseCase {

    // Function to generate a simple PDF document
    @SuppressLint("NewApi")
    operator fun invoke(
        context: Context,
        documentTitle: String,
        documentContent: List<String>,
        pagesCount: Int,
        fontSize: Float
    ) {
        // Define the page height
        val pageHeight = 2970

        // Define the page width
        val pageWidth = 2100

        // Create a new PDF document
        val pdfDocument = PdfDocument()

        // Paint object for title text
        val title = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 50F

            color = Color.Black.toArgb()
        }

        // Paint object for content text
        val content = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = fontSize
            color = Color.Black.toArgb()
        }

        // Paint object for footer text
        val footer = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = fontSize * 0.8f
            color = Color.DarkGray.toArgb()
        }

        repeat(pagesCount) { pageNumber ->
            // Paint object for drawing
            val paint = Paint()

            // Load WIRIN logo image from resources
            val bmp: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wirin_50)

            // Resize the image
            val scaledBitmap: Bitmap = bmp.scale(150, 150, false)

            // Create a PDF page with specified width and height
            val myPageInfo = PageInfo.Builder(pageWidth, pageHeight, pagesCount).create()
            val myPage = pdfDocument.startPage(myPageInfo)

            // Get the canvas to draw on
            val canvas: Canvas = myPage.canvas

            // Draw the image onto the PDF
            canvas.drawBitmap(scaledBitmap, 56f, 56f, paint)

            // Draw text onto the PDF
            canvas.drawText(documentTitle, 250F, 140F, title)

            documentContent[pageNumber].split("\n").forEachIndexed { index, line ->
                canvas.drawText(line, 100F, 300F + (index * fontSize * 1.5F), content)
            }

            canvas.drawText(
                "Documento generado automaticamente por WIRIN.",
                70F,
                canvas.height - 56f,
                footer.apply { textAlign = Paint.Align.LEFT })
            canvas.drawText(
                "Pagina ${pageNumber + 1}",
                canvas.width - 100f,
                canvas.height - 56f,
                footer.apply { textAlign = Paint.Align.RIGHT })

            // Finish writing to the page
            pdfDocument.finishPage(myPage)
        }

        val contentResolver = context.contentResolver
        val downloadsUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            // TODO: Handle pre-Q versions
            Uri.EMPTY
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$documentTitle.pdf")
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = contentResolver.insert(downloadsUri, contentValues)

        try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            } ?: throw Exception("Failed to create file URI")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }
}
