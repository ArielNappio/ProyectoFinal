package com.example.proyectofinal.domain.usecase

import com.example.proyectofinal.data.remoteData.repository.RemoteRepository

class DeleteOrderUseCase(private val remoteRepository: RemoteRepository) {
    operator fun invoke(id: Int) = remoteRepository.deleteOrder(id)
}