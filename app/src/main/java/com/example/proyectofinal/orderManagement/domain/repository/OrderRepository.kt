package com.example.proyectofinal.orderManagement.domain.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getTasks(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>>
    suspend fun toggleFavorite(orderId: String, isFavorite: Boolean)
}
