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
            orderProvider.getOrdersManagement(studentId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val cachedEntities = orderDao.getTasksByStudent(studentId).first()
                        val apiEntities = response.data!!.map { it.toEntity(studentId) }

                        val mergedEntities = apiEntities.map { apiEntity ->
                            val cachedEntity = cachedEntities.find { it.id == apiEntity.id }
                            if (cachedEntity != null) {
                                apiEntity.copy(isFavorite = cachedEntity.isFavorite)
                            } else {
                                apiEntity
                            }
                        }

                        orderDao.saveTaskGroup(mergedEntities)

                        // ✅ EMITIR lista mergeada
                        val mergedDomain = mergedEntities.map { it.toTaskGroup() }
                        emit(NetworkResponse.Success(mergedDomain))
                    }

                    is NetworkResponse.Failure -> {
                        val cached = orderDao.getTasksByStudent(studentId).first()
                        if (cached.isNotEmpty()) {
                            val taskGroups = cached.map { it.toTaskGroup() }
                            emit(NetworkResponse.Success(taskGroups))
                        } else {
                            emit(response)
                        }
                    }

                    is NetworkResponse.Loading -> { /* opcional */ }
                }
            }
        } catch (e: Exception) {
            val cached = orderDao.getTasksByStudent(studentId).first()
            if (cached.isNotEmpty()) {
                val taskGroups = cached.map { it.toTaskGroup() }
                emit(NetworkResponse.Success(taskGroups))
            } else {
                emit(NetworkResponse.Failure(e.localizedMessage ?: "Error desconocido"))
            }
        }
    }


    override suspend fun toggleFavorite(orderId: String, isFavorite: Boolean) {
        orderDao.updateFavoriteStatus(orderId, isFavorite)
    }

}
