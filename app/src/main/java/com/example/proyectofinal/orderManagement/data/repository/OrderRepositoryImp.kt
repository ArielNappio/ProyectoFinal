package com.example.proyectofinal.orderManagement.data.repository

import android.util.Log
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.entity.OrderEntity
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

        // 1. Emito cache local rápido para mejorar experiencia UI
        val cachedEntities = orderDao.getTasksByStudent(studentId).first()
        if (cachedEntities.isNotEmpty()) {
            val cachedDomain = cachedEntities.map { it.toTaskGroup() }
            emit(NetworkResponse.Success(cachedDomain))
        }

        try {
            // 2. Traigo datos remotos y hago merge
            orderProvider.getOrdersManagement(studentId).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        val apiEntities = response.data!!.map { it.toEntity(studentId) }
                        val mergedEntities = apiEntities.map { apiEntity ->
                            val cachedEntity = cachedEntities.find { it.id == apiEntity.id }
                            mergeEntities(apiEntity, cachedEntity)
                        }

                        // 3. Guardo mergeado en DB
                        orderDao.saveTaskGroup(mergedEntities)

                        // 4. Emito resultado actualizado
                        val mergedDomain = mergedEntities.map { it.toTaskGroup() }
                        emit(NetworkResponse.Success(mergedDomain))
                        Log.d("RESPONSE GET ORDERS", "Success - Actualizando ui con nueva data q trajimos de la api wachin")

                    }

                    is NetworkResponse.Failure -> {
                        Log.d("RESPONSE GET ORDERS", "failure del response de internet")
                        }

                    is NetworkResponse.Loading -> {
                        Log.d("RESPONSE GET ORDERS", "Loading...")
                    }
                }
            }
        } catch (e: Exception) {
            // En error remoto, si no hay cache, emitimos failure
            if (cachedEntities.isEmpty()) {
                emit(NetworkResponse.Failure(e.localizedMessage ?: "Error desconocido"))
            }
        }
    }

    override suspend fun toggleFavorite(orderId: String, isFavorite: Boolean) {
        orderDao.updateFavoriteStatus(orderId, isFavorite)
    }

    private fun mergeEntities(
        newEntity: OrderEntity,
        cachedEntity: OrderEntity?
    ): OrderEntity {
        if (cachedEntity == null) return newEntity
        return newEntity.copy(
            isFavorite = cachedEntity.isFavorite,
            ordersJson = cachedEntity.ordersJson, // o hacer merge JSON más inteligente si querés
            // o cualquier otro campo local que quieras conservar
        )
    }

    override suspend fun markOrderAsCommented(studentId: String, orderId: String) {
        // 1. Obtener la entidad actual
        val entity = orderDao.getTaskByIdOnce(studentId, orderId) ?: return

        // 2. Convertir entity a dominio
        val domain = entity.toTaskGroup()

        // 3. Modificar la lista orders para poner hasComments = true en el order específico
        val updatedOrders = domain.orders.map { orderStudent ->
            if (orderStudent.id.toString() == orderId) {
                orderStudent.copy(hasComments = true)
            } else {
                orderStudent
            }
        }

        // 4. Crear un nuevo dominio actualizado
        val updatedDomain = domain.copy(orders = updatedOrders)

        // 5. Convertir a entidad para guardar
        val updatedEntity = updatedDomain.toEntity(domain.studentId)

        // 6. Guardar la entidad actualizada en DB
        orderDao.saveTaskGroup(listOf(updatedEntity))
    }

}
