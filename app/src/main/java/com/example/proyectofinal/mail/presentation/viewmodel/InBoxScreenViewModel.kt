package com.example.proyectofinal.mail.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveMessageUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class InboxViewModel(
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase,
    private val receiveMessageUseCase: ReceiveMessageUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    private val _draftMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val draftMessages: StateFlow<List<MessageModel>> = _draftMessages

    private val _receivedMessages =
        MutableStateFlow<NetworkResponse<List<MessageModel>>>(NetworkResponse.Loading())
    val receivedMessages: StateFlow<NetworkResponse<List<MessageModel>>> = _receivedMessages

    private var currentUserId: String? = null
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    init {
        initInbox()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentUserId(userId: String?) {
        if (userId == null) return
        currentUserId = userId
        Log.d("InboxViewModel", "setCurrentUserId: $userId")
        initInbox() // recargar todo
        loadDraftMessages()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initInbox() {
        viewModelScope.launch {
            val userId = tokenManager.userId.first()
            currentUserId = userId
            Log.d("InboxViewModel", "UserId obtenido: $userId")

            getUserUseCase().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val user = response.data?.find { it.id == userId }
                        val email = user?.email
                        _userEmail.value = email
                        Log.d("InboxViewModel", "Email encontrado: $email")

                        if (email != null) {
                            loadMessages(userId.toString(), email)
                        }
                    }

                    is NetworkResponse.Failure -> {
                        Log.e("InboxViewModel", "Error al buscar usuario: ${response.error}")
                    }

                    is NetworkResponse.Loading -> {
                        Log.d("InboxViewModel", "Buscando usuario...")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMessages(userId: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("InboxViewModel", "Cargando mensajes para userId: $userId y email: $email")

            receiveMessageUseCase(userId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val allMessages = response.data ?: emptyList()
                        _receivedMessages.value = response

                        val inbox = allMessages
                            .filter { it.userToId == userId }
//                            .sortedByDescending { parseDate(it.date) }

                        val outbox = allMessages
                            .filter { it.userToId != userId }
//                            .sortedByDescending { parseDate(it.date) }

                        _inboxMessages.value = inbox
                        _outboxMessages.value = outbox

                        Log.d("InboxViewModel", "Mensajes de entrada: ${inbox.size}")
                        Log.d("InboxViewModel", "Mensajes de salida: ${outbox.size}")
                    }

                    is NetworkResponse.Failure -> {
                        Log.e("InboxViewModel", "Error al cargar mensajes: ${response.error}")
                        _receivedMessages.value = response
                    }

                    is NetworkResponse.Loading -> {
                        Log.d("InboxViewModel", "Cargando mensajes...")
                        _receivedMessages.value = response
                    }
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun parseDate(dateString: String): LocalDateTime {
//        return try {
//            Instant.parse(dateString).atZone(ZoneId.systemDefault()).toLocalDateTime()
//        } catch (e: Exception) {
//            Log.e("InboxViewModel", "Error parseando fecha: $dateString", e)
//            LocalDateTime.MIN
//        }
//    }


    private fun loadDraftMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            val drafts: List<MessageModel> = getDraftMessagesUseCase()
            _draftMessages.value = drafts
            if (drafts.isEmpty()) {
                println("Sin borradores")
            } else {
                println("Borradores cargados: ${drafts.size}")
            }
        }
    }

    fun discardDraft(draftId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteDraftUseCase(draftId)
            loadDraftMessages()
        }
    }
}

