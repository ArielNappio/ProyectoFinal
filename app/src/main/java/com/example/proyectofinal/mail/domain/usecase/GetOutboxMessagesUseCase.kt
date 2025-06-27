package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import kotlinx.coroutines.flow.Flow

class GetOutboxMessagesUseCase(private val messageRepository: MailRepository) {
    suspend operator fun invoke(userId: String): Flow<NetworkResponse<List<MessageModel>>>  {
        return messageRepository.getOutboxMessages(userId)
    }
}