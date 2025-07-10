package com.example.proyectofinal.orderManagement.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import kotlinx.coroutines.flow.Flow

interface OrderManagementProvider {
    fun getOrdersManagement(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>>
}