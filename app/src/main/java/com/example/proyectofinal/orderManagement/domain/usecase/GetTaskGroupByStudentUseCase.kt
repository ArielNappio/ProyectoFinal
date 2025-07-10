package com.example.proyectofinal.orderManagement.domain.usecase

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import kotlinx.coroutines.flow.Flow
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository

class GetTaskGroupByStudentUseCase(private val repository: OrderRepository) {
    operator fun invoke(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>> {
        return repository.getTasks(studentId)
    }
}