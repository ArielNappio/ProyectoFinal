package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider

class CreateOrderUseCase(private val OrderRemoteRepository: OrderProvider) {
    operator fun invoke(order: Order) = OrderRemoteRepository.createOrder(order)
}