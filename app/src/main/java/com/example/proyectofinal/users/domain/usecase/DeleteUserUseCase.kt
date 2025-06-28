package com.example.proyectofinal.users.domain.usecase

import com.example.proyectofinal.users.data.provider.UserProviderImpl

class DeleteUserUseCase(private val deleteUserRemoteRepository: UserProviderImpl ) {
    operator fun invoke(id: String) = deleteUserRemoteRepository.deleteUser(id)
}

