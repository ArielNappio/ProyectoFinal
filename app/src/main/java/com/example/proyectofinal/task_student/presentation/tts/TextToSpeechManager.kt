package com.example.proyectofinal.task_student.presentation.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File


class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private var currentIndex = 0
    private val utteranceIdPrefix = "frase_"
    private var isPaused = false

    private val _isStoped = MutableStateFlow(false)
    val isStoped: StateFlow<Boolean> = _isStoped.asStateFlow()

    private val frases = mutableListOf<String>()

    init {
        tts = TextToSpeech(context.applicationContext, this)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("TTS", "Comenzando a hablar: $utteranceId")
            }

            override fun onDone(utteranceId: String?) {
                Log.d("TTS", "Terminó de hablar: $utteranceId")
                if (!isPaused) {
                    val nextIndex = utteranceId?.removePrefix(utteranceIdPrefix)?.toIntOrNull()?.plus(1)
                    if (nextIndex != null && nextIndex < frases.size) {
                        currentIndex = nextIndex
                        speakFrase(currentIndex)
                    }
                    else _isStoped.value = true
                }
            }

            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Error al hablar: $utteranceId")
            }
        })
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "AR")
            isReady = true
            Log.d("TTS", "TTS inicializado correctamente")
        } else {
            Log.e("TTS", "Error al inicializar TTS")
        }
    }

    fun speak(text: String) {
        if (!isReady) return

        frases.clear()
        frases.addAll(dividirEnFrases(text))
        currentIndex = 0

        speakFrase(currentIndex)
    }

    private fun speakFrase(index: Int) {
        if (index >= frases.size) return

        val params = Bundle()
        val id = utteranceIdPrefix + index
        tts?.speak(frases[index], TextToSpeech.QUEUE_FLUSH, params, id)
    }

    fun pause() {
        isPaused = true
        tts?.stop()
    }

    fun resume() {
        if (isPaused && currentIndex < frases.size) {
            isPaused = false
            speakFrase(currentIndex)
        }
    }

    fun shutdown() {
        tts?.stop()
        isPaused = false
        currentIndex = 0
        frases.clear()
    }

    private fun dividirEnFrases(texto: String): List<String> {
        return texto.split(Regex("(?<=[.?!])\\s+")).filter { it.isNotBlank() }
    }

    fun resetStoppedFlag(){
        _isStoped.value = false
    }

    fun generateAudio(text: String, file: File, onComplete: () -> Unit = {}) {
        if (!isReady) return

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_export")
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "tts_export") {
                    onComplete()
                }
            }

            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Error al generar audio")
            }
        })

        tts?.synthesizeToFile(text, params, file, "tts_export")
    }

}