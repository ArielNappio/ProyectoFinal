package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val getInboxMessagesUseCase: GetInboxMessagesUseCase,
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    // Guarda el ID del usuario logueado
    private var currentUserId: Int? = null

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
        loadInboxMessages()
        loadOutboxMessages()
    }

    private fun loadInboxMessages() {
        currentUserId?.let { id ->
            viewModelScope.launch {
                val messages = getInboxMessagesUseCase(id)
                _inboxMessages.value = messages
            }
        }
    }

    private fun loadOutboxMessages() {
        currentUserId?.let { id ->
            viewModelScope.launch {
                val messages = getOutboxMessagesUseCase(id)
                _outboxMessages.value = messages
            }
        }
    }
}
