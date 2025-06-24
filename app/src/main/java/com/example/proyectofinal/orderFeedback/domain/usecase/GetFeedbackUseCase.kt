package com.example.proyectofinal.orderFeedback.domain.usecase

import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity
import com.example.proyectofinal.orderFeedback.domain.local.FeedbackRepository

class GetFeedbackUseCase (private val repository: FeedbackRepository){
    suspend operator fun invoke(orderId: Int): FeedbackEntity? = repository.getFeedback(orderId)
}