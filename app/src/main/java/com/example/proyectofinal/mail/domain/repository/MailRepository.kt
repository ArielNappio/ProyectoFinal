package com.example.proyectofinal.mail.domain.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel

interface MailRepository {
    suspend fun sendMessage(message: MessageModel): NetworkResponse<MessageModel>
    suspend fun receiveLastMessage(): NetworkResponse<MessageModel>
    suspend fun saveDraft(message: MessageModel)
    suspend fun getDrafts(): List<MessageModel>
    suspend fun deleteDraftById(idMessage: Int)
    suspend fun getInboxMessages(currentUserId: String): List<MessageModel>
    suspend fun getOutboxMessages(currentUserId: String): List<MessageModel>
    suspend fun getDraftById(idMessage: Int): MessageModel
}
