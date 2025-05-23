package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.domain.provider.OrderProvider

class GetOrdersUseCase(private val orderProvider: OrderProvider) {
    operator fun invoke() = orderProvider.getOrders()
}