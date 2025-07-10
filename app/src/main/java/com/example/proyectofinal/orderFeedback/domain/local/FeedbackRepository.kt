package com.example.proyectofinal.orderFeedback.domain.local

import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity

interface FeedbackRepository {
    suspend fun saveFeedback(orderId: Int, wasSent: Boolean)
    suspend fun getFeedback(orderId: Int): FeedbackEntity?
}