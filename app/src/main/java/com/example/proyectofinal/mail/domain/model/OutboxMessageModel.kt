package com.example.proyectofinal.mail.domain.model

data class OutboxMessageModel(
    val message: MessageModel,
    val status: MessageStatus
) {
    enum class MessageStatus {
        LEIDO, NO_LEIDO, ENTREGADO
    }
}

