package com.example.proyectofinal.mail.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import kotlinx.coroutines.flow.Flow

interface MailProvider {

    fun sendMessage(message: MessageModel): Flow<NetworkResponse<MessageModel>>
    fun receiveMessage(): Flow<NetworkResponse<MessageModel>>
    fun saveDraft(): Flow<NetworkResponse<Unit>>
    fun deleteDraft(): Flow<NetworkResponse<Unit>>

}