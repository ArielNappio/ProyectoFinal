package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDraftByIdUseCase(
    private val mailRepository: MailRepository
) {
    suspend operator fun invoke(id: Int): MessageModel {
        return withContext(Dispatchers.IO) {
            mailRepository.getDraftById(id)
        }
    }
}
