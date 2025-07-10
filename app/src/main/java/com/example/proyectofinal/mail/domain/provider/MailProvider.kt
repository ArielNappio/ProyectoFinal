package com.example.proyectofinal.mail.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import kotlinx.coroutines.flow.Flow

interface MailProvider {
    fun sendMessage(message: MessageModel): Flow<NetworkResponse<MessageModel>>
    fun receiveMessageByUserId(userId: String): Flow<NetworkResponse<List<MessageModel>>>
    fun updateMessage(message: MessageModel): Flow<NetworkResponse<Unit>>
    fun receiveMessageOutbox(): Flow<NetworkResponse<List<MessageModel>>>
    fun receiveAllConversations(): Flow<NetworkResponse<List<MessageModelDto>>>
    fun receiveConversationById(conversationId: String): Flow<NetworkResponse<List<MessageModelDto>>>
}