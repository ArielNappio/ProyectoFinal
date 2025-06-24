package com.example.proyectofinal.mail.presentation.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

class MessageViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val getDraftByIdUseCase: GetDraftByIdUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

//    init {
//        loadUserId()
//        println("cargo el userID")
//        getLibrarianEmails()
//    }

    private val _to = MutableStateFlow("")
    val to: StateFlow<String> = _to

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _formPath = MutableStateFlow<String?>(null)
    val formPath: StateFlow<String?> = _formPath

    private val _attachments = MutableStateFlow<List<String>>(emptyList())
    val attachments: StateFlow<List<String>> = _attachments

    private val _sendMessageState =
        MutableStateFlow<NetworkResponse<Unit>>(NetworkResponse.Loading())
    val sendMessageState: StateFlow<NetworkResponse<Unit>> = _sendMessageState

    private val _messageErrorEvent = MutableSharedFlow<String>()
    val messageErrorEvent = _messageErrorEvent.asSharedFlow()

    private val _draftErrorEvent = MutableSharedFlow<String>()
    val draftErrorEvent = _draftErrorEvent.asSharedFlow()

    private val _draftSavedEvent = MutableSharedFlow<Unit>()
    val draftSavedEvent = _draftSavedEvent.asSharedFlow()

    private val _draftId = MutableStateFlow(0)
    private val draftId: StateFlow<Int> = _draftId

    private val _voiceToText = MutableStateFlow("")
    val voiceToText = _voiceToText.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _userToId = MutableStateFlow("")
    val userToId: StateFlow<String> get() = _userToId

    private val _librarianEmails = MutableStateFlow<List<String>>(emptyList())
    val librarianEmails: StateFlow<List<String>> = _librarianEmails

    fun loadUserId() {
        viewModelScope.launch {
            val userIdValue = tokenManager.userId.first()
            _currentUserId.value = userIdValue.toString()
            println("${_currentUserId.value} es el userid")
        }
    }

    fun getUserIdByEmail(email: String) {
        viewModelScope.launch {
            getUserUseCase().collect { response ->
                val user = response.data?.find { it.email == email }
                _userToId.value = user?.id ?: ""
            }
        }
    }

    fun getEmailByUserId(userId: String) {
        viewModelScope.launch {
            getUserUseCase().collect { response ->
                val user = response.data?.find { it.id == userId }
                _to.value = user?.email ?: ""
            }
        }
    }

    fun getLibrarianEmails() {
        viewModelScope.launch {
            getUserUseCase().collect { response ->
                val librarians = response.data
                    ?.filter { it.roles.contains("Bibliotecario") }
                    ?.map { it.email }
                    ?: emptyList()

                Log.d("MessageViewModel", "Librarian emails found: $librarians")

                _librarianEmails.value = librarians
            }
        }
    }

    fun updateTo(value: String) {
        _to.value = value
    }

    fun updateSubject(value: String) {
        _subject.value = value
    }

    fun updateMessage(value: String) {
        _message.value = value
    }

    fun updateFormPath(value: String?) {
        _formPath.value = value
    }

    fun isFormValid(
        career: String,
        subject: String,
        note: String,
        chapter: String,
        date: String
    ): Boolean {
        return career.isNotBlank() && subject.isNotBlank() && note.isNotBlank() &&
                chapter.isNotBlank() && date.isNotBlank()
    }

    private fun isDraftValid(): Boolean {
        return to.value.isNotBlank() || subject.value.isNotBlank() || message.value.isNotBlank() || formPath.value != null
    }

    private fun isMessageValid(): Boolean {
        return to.value.isNotBlank() && subject.value.isNotBlank() && message.value.isNotBlank()
    }

    fun appendToMessage(newText: String) {
        _message.value += " $newText"
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (!isMessageValid()) {
                _messageErrorEvent.emit("Por favor, completa todos los campos antes de enviar.")
                return@launch
            }

            if (to.value !in _librarianEmails.value) {
                _messageErrorEvent.emit("El destinatario debe ser un bibliotecario.")
                return@launch
            }

            val messageModel = MessageModel(
                id = UUID.randomUUID().hashCode(),
                sender = to.value,
                subject = subject.value,
                content = message.value,
                date = Date().toString(),
                file = formPath.value ?: "",
                isDraft = false,
                isResponse = false,
                userToId = _userToId.value,
                userFromId = _currentUserId.value
            )

            Log.d("MessageViewModel", "Message sent: $messageModel")

            _sendMessageState.value = NetworkResponse.Loading()
            sendMessageUseCase(messageModel).collect { response ->
                Log.d("SendMessageDebug", "Response: $response")
                _sendMessageState.value = response
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveDraft() {
        viewModelScope.launch {
            val draftMessage = MessageModel(
                sender = to.value,
                subject = subject.value,
                content = message.value,
                date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toString(),
                id = draftId.value,
                file = formPath.value,
                isDraft = true,
                isResponse = false,
                userToId = "",
                userFromId = ""
            )
            if (isDraftValid()) {
                saveDraftUseCase(draftMessage)
                Log.d("Save draft", draftMessage.content)
                Log.d("Save draft", "success")
                _draftSavedEvent.emit(Unit)
            } else {
                Log.e("SaveDraft", "Borrador vacío, no se guarda")
                _draftErrorEvent.emit("No se pudo guardar el borrador: está vacío.")
            }
        }
    }

    fun loadDraft(id: Int) {
        viewModelScope.launch {
            val draft = getDraftByIdUseCase(id)
            _to.value = draft.sender
            _subject.value = draft.subject
            _message.value = draft.content
            _draftId.value = draft.id
        }
    }

    fun discardDraft(draftId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteDraftUseCase(draftId)
        }
    }

    fun saveFormToFile(
        context: Context,
        career: String,
        subject: String,
        note: String,
        chapter: String,
        date: String
    ): File {
        viewModelScope.launch {
            if (isFormValid(career, subject, note, chapter, date)) {
                val file = File("")
                Log.d("SaveFormDraft", "Archivo guardado en: ${file.absolutePath}")
            } else {
                Log.e("SaveFormDraft", "Formulario incompleto, no se guarda")
            }
        }
        return File("")
    }

    fun removeAttachment() {
        _formPath.value = null
        _attachments.value = emptyList()
    }

    fun addAttachment(filePath: String) {
        val currentAttachments = _attachments.value.toMutableList()
        currentAttachments.add(filePath)
        _attachments.value = currentAttachments
    }
}
