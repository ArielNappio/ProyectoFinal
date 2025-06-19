package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository

class GetOutboxMessagesUseCase(private val repository: MailRepository) {
    suspend operator fun invoke(id: String): List<MessageModel> {
        return repository.getOutboxMessages(
            currentUserId = id
        )
    }
}