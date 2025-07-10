package com.example.proyectofinal.task_student.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import java.util.Locale
import java.util.concurrent.CountDownLatch

class DownloadAsMp3UseCase {

    operator fun invoke(context: Context, documentTitle: String, documentContent: String) {
        val contentResolver = context.contentResolver
        val downloadsUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            // TODO("VERSION.SDK_INT < Q")
            Uri.EMPTY
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$documentTitle.mp3")
            put(MediaStore.Downloads.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = contentResolver.insert(downloadsUri, contentValues)

        try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    val audioData = textToSpeech(context, documentContent)
                    outputStream.write(audioData)
                }
            } ?: throw Exception("Failed to create file URI")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun textToSpeech(context: Context, text: String): ByteArray {
        var tts: TextToSpeech? = null
        val initLatch = CountDownLatch(1)
        val completionLatch = CountDownLatch(1)

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "AR")
            }
            initLatch.countDown()
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "tts_export") {
                    completionLatch.countDown()
                }
            }

            override fun onError(utteranceId: String?) {
                completionLatch.countDown()
            }
        })

        // Wait for TextToSpeech initialization
        initLatch.await()

        val fileName = "${context.cacheDir}/temp_audio.mp3"
        val file = File(fileName)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_export")
        }

        val result = tts.synthesizeToFile(
            text,
            params,
            file,
            "tts_export"
        )

        // Wait for TTS processing to complete
        completionLatch.await()

        tts.shutdown()

        if (result == TextToSpeech.SUCCESS) {
            return file.readBytes()
        } else {
            throw Exception("Failed to generate audio")
        }
    }

}