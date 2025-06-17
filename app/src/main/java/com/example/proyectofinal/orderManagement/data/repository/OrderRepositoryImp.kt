package com.example.proyectofinal.orderManagement.data.repository

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.local.OrderDao
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.mapper.toEntity
import com.example.proyectofinal.orderManagement.mapper.toTaskGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(
    private val orderProvider: OrderManagementProvider,
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getTasks(studentId: String): Flow<NetworkResponse<List<OrderDelivered>>> = flow {
        emit(NetworkResponse.Loading())

        try {
            val response = orderProvider.getOrdersManagement(studentId).first()

            if (response is NetworkResponse.Success<*>) {
                // Guardar en Room
                val entities = response.data!!.map { it.toEntity(studentId) }
                orderDao.saveTaskGroup(entities)
                emit(response)
            } else {
                // Leer local
                val cached = orderDao.getTasksByStudent(studentId).first()
                if (cached.isNotEmpty()) {
                    val taskGroups = cached.map { it.toTaskGroup() }
                    emit(NetworkResponse.Success(taskGroups))
                } else {
                    emit(response)
                }
            }
        } catch (e: Exception) {
            // En error, leer local
            val cached = orderDao.getTasksByStudent(studentId).first()
            if (cached.isNotEmpty()) {
                val taskGroups = cached.map { it.toTaskGroup() }
                emit(NetworkResponse.Success(taskGroups))
            } else {
                emit(NetworkResponse.Failure(e.localizedMessage ?: "Error desconocido"))
            }
        }
    }

}
