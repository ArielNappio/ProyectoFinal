package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.data.repository.OrderRepository

class DeleteOrderUseCase(private val authRemoteRepository: OrderRepository) {
    operator fun invoke(id: Int) = authRemoteRepository.deleteOrder(id)
}