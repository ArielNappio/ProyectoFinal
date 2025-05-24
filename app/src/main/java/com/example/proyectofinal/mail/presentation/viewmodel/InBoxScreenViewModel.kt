package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.mail.domain.MessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel() : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages: StateFlow<List<MessageModel>> = _messages.asStateFlow()

    init {
        fetchMessages()
    }

    fun fetchMessages() {
        viewModelScope.launch {
//            val fetchedMessages = messageRepository.getAllMessages()
//            _messages.value = fetchedMessages
        }
    }

    fun addMessage(message: MessageModel) {
        _messages.value = _messages.value + message
    }
}
