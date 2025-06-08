package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import kotlinx.coroutines.flow.Flow

class UpdateMessageUseCase(private val mailRemoteProvider: MailProvider) {
    operator fun invoke(message: MessageModel): Flow<NetworkResponse<Unit>> =
        mailRemoteProvider.updateMessage(message)
}
