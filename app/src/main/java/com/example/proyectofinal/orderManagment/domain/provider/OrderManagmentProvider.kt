package com.example.proyectofinal.orderManagment.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.domain.model.OrderDelivered
import kotlinx.coroutines.flow.Flow

interface OrderManagmentProvider {
    fun getOrdersManagment(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>>
}