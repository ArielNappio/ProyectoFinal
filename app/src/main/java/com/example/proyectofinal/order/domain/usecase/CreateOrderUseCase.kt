package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.data.repository.OrderRepository

class CreateOrderUseCase(private val OrderRemoteRepository: OrderRepository) {
    operator fun invoke(order: Order) = OrderRemoteRepository.createOrder(order)
}