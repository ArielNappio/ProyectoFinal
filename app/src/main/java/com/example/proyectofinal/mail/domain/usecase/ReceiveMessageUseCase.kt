package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import kotlinx.coroutines.flow.Flow

class ReceiveMessageUseCase(
    private val messageRepository: MailRepository
) {
     operator fun invoke(userEmail: String): Flow<NetworkResponse<List<MessageModel>>> {
        return messageRepository.receiveMessages(userEmail)
    }
}