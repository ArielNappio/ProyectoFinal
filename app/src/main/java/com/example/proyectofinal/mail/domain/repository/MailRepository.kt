package com.example.proyectofinal.mail.domain.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import kotlinx.coroutines.flow.Flow

interface MailRepository {
    suspend fun sendMessage(message: MessageModel): NetworkResponse<MessageModel>
    //message type INBOX
    fun receiveMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>>

    //message type OUTBOX
    fun receiveOutboxMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>>
    suspend fun saveDraft(message: MessageModel)
    suspend fun getDrafts(): List<MessageModel>
    suspend fun deleteDraftById(idMessage: Int)
    suspend fun getDraftById(idMessage: Int): MessageModel
    suspend fun getAllConversations(): Flow<NetworkResponse<List<MessageModelDto>>>
    suspend fun getConversationById(id: String): Flow<NetworkResponse<List<MessageModelDto>>>
}
