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
import com.example.proyectofinal.mail.domain.usecase.ReceiveOutboxMessageUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class InboxViewModel(
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase,
    private val receiveMessageUseCase: ReceiveMessageUseCase,
    private val receiveOutboxMessagesUseCase: ReceiveOutboxMessageUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _inboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val inboxMessages: StateFlow<List<MessageModel>> = _inboxMessages

    private val _outboxMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val outboxMessages: StateFlow<List<MessageModel>> = _outboxMessages

    private val _draftMessages = MutableStateFlow<List<MessageModel>>(emptyList())
    val draftMessages: StateFlow<List<MessageModel>> = _draftMessages

    private val _receivedInboxMessages =
        MutableStateFlow<NetworkResponse<List<MessageModel>>>(NetworkResponse.Loading())
    val receivedInboxMessages: StateFlow<NetworkResponse<List<MessageModel>>> = _receivedInboxMessages

    private val _receivedOutboxMessages =
        MutableStateFlow<NetworkResponse<List<MessageModel>>>(NetworkResponse.Loading())
    val receivedOutboxMessages: StateFlow<NetworkResponse<List<MessageModel>>> = _receivedOutboxMessages

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

    private fun loadMessages(userId: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("InboxViewModel", "Cargando mensajes para userId: $userId y email: $email")

            receiveMessageUseCase(userId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val allMessages = response.data ?: emptyList()
                        _receivedInboxMessages.value = response
                        Log.d("InboxViewModel", "Response completa:\n${response.data}")

                        val inbox = allMessages
                            .filter { it.userToId == userId }
                            .sortedByDescending { parseDate(it.date) }

                        val inboxFormatted = inbox.map { message ->
                            val parsedDate = parseDate(message.date)
                            val formattedDate = formatDateTimeForDisplay(parsedDate)
                            message.copy(date = formattedDate) // si querés sobreescribir date para mostrarlo
                        }

                        _inboxMessages.value = inboxFormatted

                        Log.d("InboxViewModel", "Mensajes de entrada: ${inbox.size}")
                    }

                    is NetworkResponse.Failure -> {
                        Log.e("InboxViewModel", "Error al cargar mensajes inbox: ${response.error}")
                        _receivedOutboxMessages.value = response
                    }

                    is NetworkResponse.Loading -> {
                        Log.d("InboxViewModel", "Cargando mensajes inbox...")
                        _receivedOutboxMessages.value = response
                    }
                }
            }

            receiveOutboxMessagesUseCase().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val allMessages = response.data ?: emptyList()
                        _receivedOutboxMessages.value = response
                        Log.d("InboxViewModel", "Response completa del outbox:\n${response.data}")

                        val outbox = allMessages
                            .filter { it.userFromId == userId }
                            .sortedByDescending { parseDate(it.date) }

                        val outboxFormatted = outbox.map { message ->
                            val parsedDate = parseDate(message.date)
                            val formattedDate = formatDateTimeForDisplay(parsedDate)
                            message.copy(date = formattedDate) // si querés sobreescribir date para mostrarlo
                        }

                        _outboxMessages.value = outboxFormatted
                        Log.d("InboxViewModel", "Mensajes de salida outbox: ${outbox.size}")
                    }

                    is NetworkResponse.Failure -> {
                        Log.e("InboxViewModel", "Error al cargar mensajes outbox: ${response.error}")
                        _receivedOutboxMessages.value = response
                    }

                    is NetworkResponse.Loading -> {
                        Log.d("InboxViewModel", "Cargando mensajes outbox...")
                        _receivedOutboxMessages.value = response
                    }
                }

            }
        }
    }

    private fun parseDate(dateString: String): LocalDateTime {
        val formatters = listOf(
            DateTimeFormatter.ISO_DATE_TIME, // 2025-06-22T22:00:43.164Z
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX"), // 2025-06-22T22:47:39.775730Z
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),    // 2025-06-22T22:47:39.775Z
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH) // Sun Jun 22 22:42:10 GMT 2025
        )

        for (formatter in formatters) {
            try {
                return try {
                    ZonedDateTime.parse(dateString, formatter).toLocalDateTime()
                } catch (_: Exception) {
                    LocalDateTime.parse(dateString, formatter)
                }
            } catch (_: Exception) {
                // pasa al siguiente formatter
            }
        }

        Log.e("InboxViewModel", "Error parseando fecha: $dateString")
        return LocalDateTime.MIN
    }

    // Esta función devuelve un string con el formato "EEE dd MMM yyyy HH:mm" (ejemplo: "Sun 22 Jun 2025 23:09")
    private fun formatDateTimeForDisplay(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm", Locale.ENGLISH)
        return dateTime.format(formatter)
    }


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

