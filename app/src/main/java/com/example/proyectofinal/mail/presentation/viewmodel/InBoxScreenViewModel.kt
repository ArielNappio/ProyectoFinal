package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.mail.domain.MessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {

   //TODO: INJECT CU EN EL CONSTRUCTOR

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    init {
        loadInboxMessages()
        loadOutboxMessages()
    }

    private fun loadInboxMessages() {
        // Lógica para cargar mensajes de la bandeja de entrada
        viewModelScope.launch {
//            val messages = inboxRepository.fetchInboxMessages()
//            _inboxMessages.value = messages
        }
    }

    private fun loadOutboxMessages() {
        // Lógica para cargar mensajes de la bandeja de salida
        viewModelScope.launch {
//            val messages = inboxRepository.fetchOutboxMessages()
//            _outboxMessages.value = messages
        }
    }

}
