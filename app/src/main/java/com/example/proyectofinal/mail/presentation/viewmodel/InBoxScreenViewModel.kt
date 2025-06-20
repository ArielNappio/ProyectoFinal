package com.example.proyectofinal.mail.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.OutboxMessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveMessageUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InboxViewModel(
    private val getInboxMessagesUseCase: GetInboxMessagesUseCase,
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase,
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase,
    private val receiveMessageUseCase: ReceiveMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    private val _draftMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val draftMessages: StateFlow<List<MessageModel>> = _draftMessages

    private val _receivedMessages = MutableStateFlow<NetworkResponse<List<MessageModel>>>(NetworkResponse.Loading())
    val receivedMessages: StateFlow<NetworkResponse<List<MessageModel>>> = _receivedMessages

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {
        viewModelScope.launch {
            val userId = tokenManager.userId.first()
            _currentUserId.value = userId
            Log.d("InboxViewModel", "UserId obtenido: $userId")
            setCurrentUserId(userId)
            MessageEvents.messageSentEvent.collect {
                loadSentMessages()
            }
        }
    }

    fun loadReceivedMessages() {
        currentUserId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                Log.d("InboxViewModel", "Cargando mensajes recibidos para userId: $id")
                receiveMessageUseCase(id.toString()).collect { response ->
                    when (response) {
                        is NetworkResponse.Success -> {
                            Log.d("InboxViewModel", "Mensajes recibidos correctamente: ${response.data?.size ?: 0}")
                        }
                        is NetworkResponse.Failure -> {
                            Log.e("InboxViewModel", "Error al cargar mensajes recibidos: ${response.error}")
                        }
                        is NetworkResponse.Loading -> {
                            Log.d("InboxViewModel", "Cargando mensajes recibidos...")
                        }
                    }
                    _receivedMessages.value = response
                }
            }
        } ?: run {
            Log.e("InboxViewModel", "currentUserId es null al cargar mensajes recibidos")
        }
    }

    fun loadSentMessages() {
        currentUserId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.d("InboxViewModel", "Cargando mensajes enviados para userId: $id")
                    val result = getInboxMessagesUseCase(id.toString())
                    _inboxMessages.value = result
                    Log.d("InboxViewModel", "Mensajes enviados cargados correctamente: ${result.size}")
                } catch (e: Exception) {
                    Log.e("InboxViewModel", "Error al cargar mensajes enviados: ${e.message}")
                    _inboxMessages.value = emptyList()
                }
            }
        } ?: run {
            Log.e("InboxViewModel", "currentUserId es null al cargar mensajes enviados")
        }
    }



    fun setCurrentUserId(userId: String?) {
        if (userId == null) return
        _currentUserId.value = userId
        Log.d("InboxViewModel", "setCurrentUserId: $userId")
//        loadInboxMessages()
//        loadOutboxMessages()
        loadDraftMessages()
        loadReceivedMessages()
        loadSentMessages()
    }

//    private fun loadInboxMessages() {
//        currentUserId?.let { id ->
//            viewModelScope.launch(Dispatchers.IO) {
//                val messages = getInboxMessagesUseCase(id)
//                _inboxMessages.value = messages
//            }
//        }
//    }

//    private fun loadOutboxMessages() {
//        currentUserId?.let { id ->
//            viewModelScope.launch(Dispatchers.IO) {
//                val messages = getOutboxMessagesUseCase(id)
//                val outboxMessages = messages.map { message ->
//                    OutboxMessageModel(
//                        message = message,
//                        status = OutboxMessageModel.MessageStatus.NO_LEIDO
//                    )
//                }
//                _outboxMessages.value = outboxMessages.map { it.message }
//            }
//        }
//    }


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