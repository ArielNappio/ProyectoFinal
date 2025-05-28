package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import kotlinx.coroutines.flow.Flow

class GetDraftMessagesUseCase(private val mailRepository: MailRepository) {
    suspend operator fun invoke(userId: Int): Flow<List<MessageModel>> {
        return mailRepository.getDrafts(userId)
    }
}