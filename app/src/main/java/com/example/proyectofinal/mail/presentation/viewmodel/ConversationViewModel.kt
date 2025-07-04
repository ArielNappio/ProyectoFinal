package com.example.proyectofinal.mail.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.domain.usecase.ReceiveConversationByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel(
    private val receiveConversationByIdUseCase: ReceiveConversationByIdUseCase
) : ViewModel() {

    private val _conversationState = MutableStateFlow<NetworkResponse<List<MessageModelDto>>>(NetworkResponse.Loading())
    val conversationState: StateFlow<NetworkResponse<List<MessageModelDto>>> = _conversationState

    fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            receiveConversationByIdUseCase(conversationId).collect { response ->
                _conversationState.value = response
            }
        }
    }
}
