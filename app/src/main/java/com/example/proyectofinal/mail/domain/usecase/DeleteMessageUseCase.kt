package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.repository.MailRepository

//TODO DEBERIA SER POR ID
class DeleteMessageUseCase (private val mailRemoteProvider: MailRepository) {
    suspend operator fun invoke() = mailRemoteProvider.deleteDrafts()
}