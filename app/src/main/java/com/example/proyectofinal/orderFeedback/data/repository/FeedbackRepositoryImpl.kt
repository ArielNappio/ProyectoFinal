package com.example.proyectofinal.orderFeedback.data.repository

import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity
import com.example.proyectofinal.orderFeedback.data.local.FeedbackDao
import com.example.proyectofinal.orderFeedback.domain.local.FeedbackRepository

class FeedbackRepositoryImpl(private val dao: FeedbackDao) : FeedbackRepository{

    override suspend fun saveFeedback(orderId: Int, wasSent: Boolean) {
        dao.insertFeedback(FeedbackEntity(orderId, wasSent))
    }

    override suspend fun getFeedback(orderId: Int): FeedbackEntity? {
        return dao.getFeedback(orderId)
    }
}