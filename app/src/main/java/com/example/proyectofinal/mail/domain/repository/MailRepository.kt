package com.example.proyectofinal.mail.domain.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import kotlinx.coroutines.flow.Flow

interface MailRepository {
    suspend fun sendMessage(message: MessageModel): NetworkResponse<MessageModel>
    suspend fun receiveLastMessage(): NetworkResponse<MessageModel>
    suspend fun saveDraft(message: MessageModel)
    suspend fun getDrafts(currentUserId: Int): Flow<List<MessageModel>>
    suspend fun deleteDrafts()
    suspend fun getInboxMessages(currentUserId: Int): List<MessageModel>
    suspend fun getOutboxMessages(currentUserId: Int): List<MessageModel>
}
