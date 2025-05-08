package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.data.repository.OrderRepository

class GetOrderByIdUseCase(private val orderRepository: OrderRepository) {
    operator fun invoke(id: Int) = orderRepository.getOrderById(id)
}