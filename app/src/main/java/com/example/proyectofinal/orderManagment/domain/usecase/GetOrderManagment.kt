package com.example.proyectofinal.orderManagment.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagment.domain.model.TaskGroup
import com.example.proyectofinal.orderManagment.domain.provider.OrderManagmentProvider
import kotlinx.coroutines.flow.Flow

class GetOrdersManagmentUseCase(private val orderProvider: OrderManagmentProvider) {
    operator fun invoke(studentId: String): Flow<NetworkResponse<List<TaskGroup>>> {
        return orderProvider.getOrdersManagment(studentId)
    }
}