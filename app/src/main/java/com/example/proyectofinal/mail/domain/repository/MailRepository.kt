package com.example.proyectofinal.mail.domain.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import kotlinx.coroutines.flow.Flow

interface MailRepository {
    suspend fun sendMessage(message: MessageModel): NetworkResponse<MessageModel>
    suspend fun receiveLastMessage(): NetworkResponse<MessageModel>
    //message type INBOX
    fun receiveMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>>
    //message type OUTBOX
    suspend fun getOutboxMessages(userId: String): Flow<NetworkResponse<List<MessageModel>>>

    suspend fun saveDraft(message: MessageModel)
    suspend fun getDrafts(): List<MessageModel>
    suspend fun deleteDraftById(idMessage: Int)
    suspend fun getInboxMessages(currentUserId: String): List<MessageModel>
    suspend fun getDraftById(idMessage: Int): MessageModel
}
