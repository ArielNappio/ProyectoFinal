package com.example.proyectofinal.auth.data.remoteData.repository

import com.example.proyectofinal.auth.data.remoteData.model.LoginRequest
import com.example.proyectofinal.auth.data.remoteData.model.LoginResponse
import com.example.proyectofinal.auth.data.remoteData.model.UserResponse
import com.example.proyectofinal.core.network.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface AuthRemoteRepository{

    fun postLogin(loginRequest: LoginRequest): Flow<NetworkResponse<LoginResponse>>
    fun getMe(token: String): Flow<NetworkResponse<UserResponse>>

}