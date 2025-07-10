package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider

class UpdateOrderUseCase(private val orderProvider: OrderProvider) {
    operator fun invoke(order: Order) = orderProvider.updateOrder(order)
}