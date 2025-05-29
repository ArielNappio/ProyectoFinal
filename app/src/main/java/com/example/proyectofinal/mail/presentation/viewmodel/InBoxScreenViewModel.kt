package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.OutboxMessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val getInboxMessagesUseCase: GetInboxMessagesUseCase,
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase,
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase,
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    private val _draftMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val draftMessages: StateFlow<List<MessageModel>> = _draftMessages

    private var currentUserId: Int? = null

    init {
        println("asdasdasdsa")

        viewModelScope.launch {
            loadDraftMessages()
        }
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
                val outboxMessages = messages.map { message ->
                    OutboxMessageModel(
                        message = message,
                        status = OutboxMessageModel.MessageStatus.NO_LEIDO
                    )
                }
                _outboxMessages.value = outboxMessages.map { it.message }
            }
        }
    }


    fun updateMessageStatus(messageId: Int, newStatus: OutboxMessageModel.MessageStatus) {

    }


    private fun loadDraftMessages() {

            viewModelScope.launch(Dispatchers.IO) {
                val drafts: List<MessageModel> = getDraftMessagesUseCase()
                if(drafts.isEmpty()){
                    println("ta vacio")
                }
                else{
                    println("algo cargo")
                }
                _draftMessages.value = drafts
            }

    }

    fun discardDraft(draftId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteDraftUseCase(draftId)
            loadDraftMessages()
        }
    }


}