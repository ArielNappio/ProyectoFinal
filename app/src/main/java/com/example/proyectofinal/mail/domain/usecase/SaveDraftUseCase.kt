package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository

class SaveDraftUseCase(private val mailRepository: MailRepository) {
    suspend operator fun invoke(message: MessageModel) = mailRepository.saveDraft(message)
}