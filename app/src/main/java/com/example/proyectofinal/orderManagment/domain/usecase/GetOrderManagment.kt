package com.example.proyectofinal.orderManagment.domain.usecase

import com.example.proyectofinal.orderManagment.domain.provider.OrderManagmentProvider

class GetOrdersManagmentUseCase(private val orderProvider: OrderManagmentProvider) {
    operator fun invoke() = orderProvider.getOrdersManagment()
}