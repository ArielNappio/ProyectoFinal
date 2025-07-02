package com.example.proyectofinal.auth.domain.usecases

import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

class GetMeUseCase(private val authRemoteProviderImpl: AuthRemoteProvider) {
    operator fun invoke(): Flow<NetworkResponse<UserResponseDto>> {
        return authRemoteProviderImpl.getMe()
    }
}