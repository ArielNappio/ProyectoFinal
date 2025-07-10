package com.example.proyectofinal.orderFeedback.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderFeedback(
    val studentId: String,
    val feedbackText: String,
    val stars: Int,
    val orderId: Int
)

@Serializable
data class FeedbackRequestWrapper(
    val request: OrderFeedback
)