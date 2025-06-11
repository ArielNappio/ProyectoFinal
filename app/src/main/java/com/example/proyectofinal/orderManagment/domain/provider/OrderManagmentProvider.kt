package com.example.proyectofinal.orderManagment.domain.provider

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.student.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface OrderManagmentProvider {
    fun getOrdersManagment(): Flow<NetworkResponse<List<Task>>>
}