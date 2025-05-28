package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository

class GetDraftMessagesUseCase(private val mailRepository: MailRepository) {
    suspend operator fun invoke(): List<MessageModel> {
        return mailRepository.getDrafts()
    }
}