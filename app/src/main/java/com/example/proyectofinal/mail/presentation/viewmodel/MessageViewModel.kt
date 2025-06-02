package com.example.proyectofinal.mail.presentation.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime

class MessageViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveDraftUseCase: SaveDraftUseCase,
    private val getDraftByIdUseCase: GetDraftByIdUseCase,
    private val deleteDraftUseCase: DeleteMessageByIdUseCase
) : ViewModel() {

    private val _to = MutableStateFlow("")
    val to: StateFlow<String> = _to

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _filePath = MutableStateFlow<String?>(null)
    val filePath: StateFlow<String?> = _filePath

    private val _sendMessageState = MutableStateFlow<NetworkResponse<Unit>>(NetworkResponse.Loading())
    val sendMessageState: StateFlow<NetworkResponse<Unit>> = _sendMessageState

    private val _draftSavedEvent = MutableSharedFlow<Unit>()
    val draftSavedEvent = _draftSavedEvent.asSharedFlow()

    private val _draftId = MutableStateFlow(0)
    private val draftId: StateFlow<Int> = _draftId

    private val _voiceToText = MutableStateFlow("")
    val voiceToText = _voiceToText.asStateFlow()

    fun updateTo(value: String) {
        _to.value = value
    }

    fun updateSubject(value: String) {
        _subject.value = value
    }

    fun updateMessage(value: String) {
        _message.value = value
    }

    fun updateFilePath(value: String?) {
        _filePath.value = value
    }

    fun appendToMessage(newText: String) {
        _message.value += " $newText"
    }

    fun sendMessage(messageModel: MessageModel) {
        viewModelScope.launch {
            _sendMessageState.value = NetworkResponse.Loading()
            sendMessageUseCase(messageModel).collect { response ->
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
                date = LocalDateTime.now().toString(),
                id = draftId.value
            )
            saveDraftUseCase(draftMessage)
            Log.d("Save draft", draftMessage.content)
            Log.d("Save draft","success")
            _draftSavedEvent.emit(Unit)
        }
    }

    fun loadDraft(id: Int) {
        viewModelScope.launch {
            val draft = getDraftByIdUseCase(id)
            draft.let {
                _to.value = it.sender
                _subject.value = it.subject
                _message.value = it.content
                _draftId.value = it.id
            }
        }
    }

    fun discardDraft(draftId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteDraftUseCase(draftId)
        }
    }

    fun saveFormToFile(
        context: Context,
        title: String,
        author: String,
        career: String,
        signature: String,
        date: String
    ): File {
        val file = File(context.cacheDir, "formulario_${System.currentTimeMillis()}.txt")
        file.writeText(
            """
        Título: $title
        Autor: $author
        Carrera: $career
        Firma: $signature
        Fecha: $date
        """.trimIndent()
        )
        return file
    }
}