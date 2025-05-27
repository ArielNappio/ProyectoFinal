package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(private val sendMessageUseCase: SendMessageUseCase) : ViewModel() {

    private val _sendMessageState = MutableStateFlow<NetworkResponse<MessageModel>?>(null)
    val sendMessageState: StateFlow<NetworkResponse<MessageModel>?> = _sendMessageState

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

    fun sendMessage(message: MessageModel) {
        viewModelScope.launch {
            sendMessageUseCase(message).collect { response ->
                _sendMessageState.value = response
            }
        }
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
