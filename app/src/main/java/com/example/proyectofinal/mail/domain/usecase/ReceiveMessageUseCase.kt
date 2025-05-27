package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider

class ReceiveMessageUseCase (private val mailRemoteProvider: MailProvider) {
    operator fun invoke() = mailRemoteProvider.receiveMessage()
}