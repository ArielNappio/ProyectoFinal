package com.example.proyectofinal.mail.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.domain.usecase.ReceiveConversationByIdUseCase
import com.example.proyectofinal.mail.util.DateUtilsMail
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
                val formattedResponse = when (response) {
                    is NetworkResponse.Success -> {
                        val formattedMessages = response.data?.map { message ->
                            message.copy(
                                date = DateUtilsMail.formatHumanReadableDate(message.date)
                            )
                        }
                        NetworkResponse.Success(formattedMessages)
                    }

                    else -> response // Error o Loading, lo pasás tal cual
                }
                _conversationState.value = formattedResponse
                Log.d("ConverViewModel", "Response completa de la conver:\n${formattedResponse.data}")
            }
        }
    }
}
