package com.example.proyectofinal.orderManagement.domain.usecase

import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository

class UpdateFavoriteStatusUseCase(val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String, isFavorite: Boolean){
        return repository.toggleFavorite(orderId, isFavorite)
    }
}