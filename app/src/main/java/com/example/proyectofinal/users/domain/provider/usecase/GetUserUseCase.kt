package com.example.proyectofinal.users.domain.provider.usecase

import com.example.proyectofinal.users.data.provider.UserProviderImpl

class GetUserUseCase(private val getUserRemoteRepository: UserProviderImpl) {
    operator fun invoke() = getUserRemoteRepository.getUsers()
}