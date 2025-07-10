package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.repository.MailRepository

class DeleteMessageByIdUseCase (private val mailRepository: MailRepository) {
    suspend operator fun invoke(idMessage: Int) = mailRepository.deleteDraftById(idMessage)
}