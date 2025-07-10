package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.domain.provider.OrderProvider

class GetOrderByIdUseCase(private val orderProvider: OrderProvider) {
    operator fun invoke(id: Int) = orderProvider.getOrderById(id)
}