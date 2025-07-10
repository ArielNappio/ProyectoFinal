package com.example.proyectofinal.auth.domain.usecases

import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

class PostLoginUseCase(
    private val authRemoteProviderImpl: AuthRemoteProvider
) {
    operator fun invoke(loginRequestDto: LoginRequestDto): Flow<NetworkResponse<LoginResponseDto>> {
        return authRemoteProviderImpl.postLogin(loginRequestDto)
    }
}