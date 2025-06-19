package com.example.proyectofinal.auth.domain.provider

import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface AuthRemoteProvider{

    fun postLogin(loginRequestDto: LoginRequestDto): Flow<NetworkResponse<LoginResponseDto>>
    fun getMe(): Flow<NetworkResponse<UserResponseDto>>

}