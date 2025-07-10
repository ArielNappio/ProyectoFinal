package com.example.proyectofinal.mail.domain.usecase

import com.example.proyectofinal.mail.domain.repository.MailRepository

//class ReceiveOutboxMessageUseCase (private val mailRemoteProvider: MailProvider) {
//    operator fun invoke() = mailRemoteProvider.receiveMessageOutbox()
//}
class ReceiveOutboxMessageUseCase(private val mailRepository: MailRepository) {
    operator fun invoke(userId: String) = mailRepository.receiveOutboxMessages(userId)
}
