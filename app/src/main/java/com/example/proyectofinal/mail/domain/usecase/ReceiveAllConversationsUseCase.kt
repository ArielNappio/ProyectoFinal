package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.domain.provider.MailProvider
import kotlinx.coroutines.flow.Flow

class ReceiveAllConversationsUseCase(
    private val provider: MailProvider
) {
    operator fun invoke(): Flow<NetworkResponse<List<MessageModelDto>>> {
        return provider.receiveAllConversations()
    }
}