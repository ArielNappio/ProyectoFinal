package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.data.repository.OrderRepository

class UpdateOrderUseCase(private val orderRepository: OrderRepository) {
    operator fun invoke(order: Order) = orderRepository.updateOrder(order)
}