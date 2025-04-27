package com.example.proyectofinal.domain.usecase

import com.example.proyectofinal.data.remoteData.model.Order
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository

class UpdateOrderUseCase(private val remoteRepository: RemoteRepository) {
    operator fun invoke(order: Order) = remoteRepository.updateOrder(order.id, order)
}