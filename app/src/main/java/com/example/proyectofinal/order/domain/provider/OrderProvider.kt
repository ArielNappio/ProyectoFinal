package com.example.proyectofinal.order.domain.provider

import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface OrderProvider {

    fun getOrders(): Flow<NetworkResponse<List<Order>>>
    fun getOrderById(id: Int): Flow<NetworkResponse<Order>>
    fun createOrder(order: Order): Flow<NetworkResponse<Order>>
    fun updateOrder(order: Order): Flow<NetworkResponse<Unit>>
    fun deleteOrder(id: Int): Flow<NetworkResponse<Unit>>

}