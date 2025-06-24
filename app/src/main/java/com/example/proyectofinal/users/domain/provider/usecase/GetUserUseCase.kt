package com.example.proyectofinal.users.domain.provider.usecase

import com.example.proyectofinal.users.domain.provider.UserProvider

class GetUserUseCase(private val getUserRemoteRepository: UserProvider) {
    operator fun invoke() = getUserRemoteRepository.getUsers()
}