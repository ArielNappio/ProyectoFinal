package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider

class SendMessageUseCase(private val mailRemoteProvider: MailProvider) {
    operator fun invoke(message: MessageModel) = mailRemoteProvider.sendMessage(message)
}