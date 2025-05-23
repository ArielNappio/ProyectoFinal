package com.example.proyectofinal.order.domain.usecase

import com.example.proyectofinal.order.domain.provider.OrderProvider

class DeleteOrderUseCase(private val authRemoteRepository: OrderProvider) {
    operator fun invoke(id: Int) = authRemoteRepository.deleteOrder(id)
}