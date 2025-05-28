package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SendMessageUseCase(private val mailRemoteProvider: MailProvider) {
    operator fun invoke(message: MessageModel): Flow<NetworkResponse<Unit>> =
        mailRemoteProvider.sendMessage(message)
            .map { response ->
                when(response) {
                    is NetworkResponse.Success -> NetworkResponse.Success(Unit)
                    is NetworkResponse.Failure -> NetworkResponse.Failure(response.error)
                    is NetworkResponse.Loading -> NetworkResponse.Loading()
                }
            }
}
