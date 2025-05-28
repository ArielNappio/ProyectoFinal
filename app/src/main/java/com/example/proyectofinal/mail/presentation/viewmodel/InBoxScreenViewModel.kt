package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val getInboxMessagesUseCase: GetInboxMessagesUseCase,
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase,
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    private val _draftMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val draftMessages: StateFlow<List<MessageModel>> = _draftMessages

    private var currentUserId: Int? = null

    init {
        loadDraftMessages()
    }

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
        loadInboxMessages()
        loadOutboxMessages()
        loadDraftMessages()
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

    private fun loadDraftMessages() {
        currentUserId?.let { id ->
            viewModelScope.launch {
                val drafts = getDraftMessagesUseCase(id)
                _draftMessages.value = drafts
            }
        }
    }

//    fun discardDraft(message: MessageModel) {
//        _draftMessages.value = _draftMessages.value.filterNot { it.id == message.id }
//    }
}
