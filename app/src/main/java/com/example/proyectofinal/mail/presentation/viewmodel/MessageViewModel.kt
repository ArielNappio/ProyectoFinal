package com.example.proyectofinal.mail.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MessageViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveDraftUseCase: SaveDraftUseCase
) : ViewModel() {

    private val _to = MutableStateFlow("")
    val to: StateFlow<String> = _to

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _sendMessageState = MutableStateFlow<NetworkResponse<Unit>>(NetworkResponse.Loading())
    val sendMessageState: StateFlow<NetworkResponse<Unit>> = _sendMessageState

    private val _draftSavedEvent = MutableSharedFlow<Unit>()
    val draftSavedEvent = _draftSavedEvent.asSharedFlow()

    fun updateTo(value: String) {
        _to.value = value
    }

    fun updateSubject(value: String) {
        _subject.value = value
    }

    fun updateMessage(value: String) {
        _message.value = value
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
                date = LocalDateTime.now().toString()
            )
            saveDraftUseCase(draftMessage)
            Log.d("Save draft", "${draftMessage.content}")
            Log.d("Save draft","success")
            _draftSavedEvent.emit(Unit)
        }
    }
}

