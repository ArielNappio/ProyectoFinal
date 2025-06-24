package com.example.proyectofinal.orderFeedback.domain.model

data class OrderFeedback(
    val studentId: String,
    val feedbackText: String,
    val stars: Int,
    val orderId: Int
)