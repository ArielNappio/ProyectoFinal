package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessageViewModel : ViewModel() {

    private val _to = MutableStateFlow("")
    val to: StateFlow<String> = _to

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun updateTo(value: String) {
        _to.value = value
    }

    fun updateSubject(value: String) {
        _subject.value = value
    }

    fun updateMessage(value: String) {
        _message.value = value
    }

    fun sendMessage() {
        // Aquí podrías usar un repositorio para enviar
        println("Enviando mensaje a: $to\nAsunto: $subject\nMensaje: $message")
        clearFields()
    }

    fun saveDraft() {
        // Guardar en local/db/memoria según quieras
        println("Guardando borrador de: $to")
    }

    fun discardMessage() {
        clearFields()
    }

    private fun clearFields() {
        _to.value = ""
        _subject.value = ""
        _message.value = ""
    }
}
