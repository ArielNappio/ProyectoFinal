package com.example.proyectofinal.orderFeedback.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderFeedback.domain.model.OrderFeedback
import kotlinx.coroutines.flow.Flow

interface OrderFeedbackProvider {
    fun sendFeedback(feedback: OrderFeedback) : Flow<NetworkResponse<OrderFeedback>>
}