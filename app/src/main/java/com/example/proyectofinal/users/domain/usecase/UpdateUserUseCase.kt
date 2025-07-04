package com.example.proyectofinal.users.domain.usecase

import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.data.provider.UserProviderImpl

class UpdateUserUseCase(private val updateUserRemoteRepository: UserProviderImpl) {
    operator fun invoke(id: String, updatedUser: User) = updateUserRemoteRepository.updateUser(id, updatedUser)
}