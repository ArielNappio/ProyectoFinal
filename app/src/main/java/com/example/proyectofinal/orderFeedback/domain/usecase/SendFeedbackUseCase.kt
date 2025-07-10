package com.example.proyectofinal.orderFeedback.domain.usecase

import com.example.proyectofinal.orderFeedback.domain.model.OrderFeedback
import com.example.proyectofinal.orderFeedback.domain.provider.OrderFeedbackProvider

class SendFeedbackUseCase(private val orderFeedbackProvider: OrderFeedbackProvider) {
    operator fun invoke(feedback: OrderFeedback) = orderFeedbackProvider.sendFeedback(feedback)
}