package com.example.proyectofinal.orderFeedback.domain.usecase

import com.example.proyectofinal.orderFeedback.domain.local.FeedbackRepository

class SaveFeedbackUseCase(private val repository: FeedbackRepository) {
    suspend operator fun invoke(orderId: Int, wasSent: Boolean) = repository.saveFeedback(orderId, wasSent)
}
