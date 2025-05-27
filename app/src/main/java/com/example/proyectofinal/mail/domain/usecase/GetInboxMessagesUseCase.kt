package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository

class GetInboxMessagesUseCase(private val repository: MailRepository) {
    suspend operator fun invoke(currentUserId: Int): List<MessageModel> {
        return repository.getInboxMessages(currentUserId)
    }
}
