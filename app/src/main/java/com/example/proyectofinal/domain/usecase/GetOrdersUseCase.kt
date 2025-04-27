package com.example.proyectofinal.domain.usecase

import com.example.proyectofinal.data.remoteData.repository.RemoteRepository

class GetOrdersUseCase(private val remoteRepository: RemoteRepository) {
    operator fun invoke() = remoteRepository.getOrders()
}